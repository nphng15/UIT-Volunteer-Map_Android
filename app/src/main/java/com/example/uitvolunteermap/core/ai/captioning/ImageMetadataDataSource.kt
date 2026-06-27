package com.example.uitvolunteermap.core.ai.captioning

import android.content.Context
import android.location.Geocoder
import android.media.ExifInterface
import android.net.Uri
import com.example.uitvolunteermap.core.common.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

/** Date + place context extracted from a photo's EXIF metadata. */
data class PhotoMeta(
    val dateLabel: String? = null,
    val placeName: String? = null
) {
    val hasAny: Boolean get() = dateLabel != null || placeName != null
}

/**
 * Reads contextual metadata embedded in the photo itself — capture date and GPS
 * location — entirely on-device. No permission needed (we only read what the
 * picked image already carries) and no network: the date is parsed from EXIF and
 * the place is resolved with the on-device [Geocoder] when offline data exists.
 *
 * Many shared/edited photos have their EXIF stripped, so every field is nullable
 * and the caller treats a result as best-effort enrichment.
 */
@Singleton
class ImageMetadataDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun read(uri: Uri): PhotoMeta = withContext(ioDispatcher) {
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                PhotoMeta(
                    dateLabel = parseDate(exif),
                    placeName = parsePlace(exif)
                )
            } ?: PhotoMeta()
        }.getOrElse {
            Timber.w(it, "Failed to read EXIF for %s", uri)
            PhotoMeta()
        }
    }

    private fun parseDate(exif: ExifInterface): String? {
        val raw = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
            ?: return null
        // EXIF format: "yyyy:MM:dd HH:mm:ss"
        val datePart = raw.substringBefore(' ').trim()
        val parts = datePart.split(':')
        if (parts.size != 3) return null
        val (y, m, d) = parts
        if (y.isBlank() || m.isBlank() || d.isBlank()) return null
        return "ngày $d/$m/$y"
    }

    private fun parsePlace(exif: ExifInterface): String? {
        // android.media.ExifInterface exposes GPS via getLatLong(float[]), not a
        // no-arg property — fill an output array and bail if there's no fix.
        val output = FloatArray(2)
        @Suppress("DEPRECATION")
        if (!exif.getLatLong(output)) return null
        val lat = output[0].toDouble()
        val lng = output[1].toDouble()
        return runCatching {
            @Suppress("DEPRECATION")
            val addresses = Geocoder(context, Locale("vi", "VN")).getFromLocation(lat, lng, 1)
            val address = addresses?.firstOrNull() ?: return@runCatching null
            // Use only proper administrative names (district/city/province).
            // Deliberately NOT featureName — that can be a street number or POI
            // string that reads as garbage in the caption.
            (address.subAdminArea ?: address.locality ?: address.adminArea)
                ?.takeIf { it.isNotBlank() && it.any(Char::isLetter) }
        }.onFailure { Timber.w(it, "Geocoder failed for %f,%f", lat, lng) }.getOrNull()
    }
}
