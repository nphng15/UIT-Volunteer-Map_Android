package com.example.uitvolunteermap.features.admin.post.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.post.domain.repository.AdminPostRepository
import javax.inject.Inject

/**
 * Gom các thao tác tạo/sửa/xoá bài viết kèm validation tại tầng domain, để ViewModel
 * không phải lặp lại logic kiểm tra. Trả về [AppResult] để UI phân nhánh thống nhất.
 *
 * Lưu ý: `teamId` và `authorId` đều là số nguyên bắt buộc (>0). `authorId` chính là
 * userId của tác giả, KHÔNG phải accId.
 * TODO: nên có team picker / author picker lấy danh sách đội & người dùng thật;
 *       hiện tại nhận đầu vào số đã validate ở form.
 */
class ManageAdminPostUseCase @Inject constructor(
    private val repository: AdminPostRepository
) {

    suspend fun create(
        title: String,
        content: String,
        teamId: Int,
        authorId: Int
    ): AppResult<Unit> {
        if (title.isBlank())
            return AppResult.Error(AppError.Validation("Tiêu đề bài viết không được để trống."))
        if (content.isBlank())
            return AppResult.Error(AppError.Validation("Nội dung bài viết không được để trống."))
        if (teamId <= 0)
            return AppResult.Error(AppError.Validation("Mã đội không hợp lệ."))
        if (authorId <= 0)
            return AppResult.Error(AppError.Validation("Mã tác giả không hợp lệ."))

        return repository.createPost(
            title = title.trim(),
            content = content.trim(),
            teamId = teamId,
            authorId = authorId
        )
    }

    suspend fun update(
        postId: Int,
        title: String?,
        content: String?,
        teamId: Int?,
        authorId: Int?
    ): AppResult<Unit> {
        if (postId <= 0)
            return AppResult.Error(AppError.Validation("Mã bài viết không hợp lệ."))
        if (title == null && content == null && teamId == null && authorId == null)
            return AppResult.Error(AppError.Validation("Cần cung cấp ít nhất một trường để cập nhật."))

        if (title != null && title.isBlank())
            return AppResult.Error(AppError.Validation("Tiêu đề bài viết không được để trống."))
        if (content != null && content.isBlank())
            return AppResult.Error(AppError.Validation("Nội dung bài viết không được để trống."))
        if (teamId != null && teamId <= 0)
            return AppResult.Error(AppError.Validation("Mã đội không hợp lệ."))
        if (authorId != null && authorId <= 0)
            return AppResult.Error(AppError.Validation("Mã tác giả không hợp lệ."))

        return repository.updatePost(
            postId = postId,
            title = title?.trim(),
            content = content?.trim(),
            teamId = teamId,
            authorId = authorId
        )
    }

    suspend fun delete(postId: Int): AppResult<Unit> {
        if (postId <= 0)
            return AppResult.Error(AppError.Validation("Mã bài viết không hợp lệ."))
        return repository.deletePost(postId)
    }
}
