package com.example.uitvolunteermap.core.ai.captioning

internal object UitPostTemplates {

    val LabelToSubject: Map<String, SubjectEntry> = mapOf(
        // People & community
        "person" to SubjectEntry("các bạn tình nguyện viên", "🤝", listOf("#TinhNguyenUIT")),
        "people" to SubjectEntry("đông đảo tình nguyện viên", "🤝", listOf("#TinhNguyenUIT")),
        "crowd" to SubjectEntry("không khí sôi nổi", "🎉", listOf("#UITVolunteer")),
        "team" to SubjectEntry("cả đội cùng đồng lòng", "🤝", listOf("#TinhNguyenUIT")),
        "smile" to SubjectEntry("những nụ cười rạng rỡ", "😊", emptyList()),
        "uniform" to SubjectEntry("màu áo đồng phục thân thương", "👕", listOf("#TinhNguyenUIT")),
        "child" to SubjectEntry("các em nhỏ thân thương", "🧒", listOf("#UITxCongDong")),
        "children" to SubjectEntry("các em nhỏ tại điểm hoạt động", "🧒", listOf("#UITxCongDong")),
        "baby" to SubjectEntry("những thiên thần nhỏ", "👶", listOf("#UITxCongDong")),
        "elder" to SubjectEntry("các cô bác lớn tuổi", "👵", listOf("#UITxCongDong")),
        "man" to SubjectEntry("các anh tình nguyện viên", "👨", emptyList()),
        "woman" to SubjectEntry("các chị tình nguyện viên", "👩", emptyList()),

        // Food & drink
        "food" to SubjectEntry("những phần ăn ấm áp", "🍱", listOf("#UITxCongDong")),
        "fast food" to SubjectEntry("những phần ăn nhẹ", "🍔", listOf("#UITxCongDong")),
        "rice" to SubjectEntry("những phần cơm nghĩa tình", "🍚", listOf("#UITxCongDong")),
        "drink" to SubjectEntry("thức uống mát lành", "🥤", listOf("#UITxCongDong")),
        "cake" to SubjectEntry("phần bánh ngọt sẻ chia", "🍰", emptyList()),
        "bread" to SubjectEntry("ổ bánh mì nghĩa tình", "🥖", emptyList()),
        "fruit" to SubjectEntry("phần hoa quả tươi ngon", "🍎", emptyList()),

        // Education
        "book" to SubjectEntry("những trang sách yêu thương", "📚", listOf("#TiepSucMuaThi")),
        "books" to SubjectEntry("tủ sách mới cho các em", "📚", listOf("#TiepSucMuaThi")),
        "paper" to SubjectEntry("những phần quà học tập", "📝", listOf("#TiepSucMuaThi")),
        "stationery" to SubjectEntry("những phần văn phòng phẩm", "✏️", listOf("#TiepSucMuaThi")),
        "pen" to SubjectEntry("nét bút đầu năm học", "🖊️", listOf("#TiepSucMuaThi")),

        // Places
        "school" to SubjectEntry("ngôi trường thân yêu", "🏫", listOf("#TiepSucMuaThi")),
        "classroom" to SubjectEntry("lớp học rộn ràng tiếng cười", "🏫", listOf("#TiepSucMuaThi")),
        "building" to SubjectEntry("điểm hoạt động hôm nay", "🏢", emptyList()),
        "house" to SubjectEntry("mái ấm nhỏ", "🏠", listOf("#UITxCongDong")),
        "library" to SubjectEntry("góc đọc sách yên tĩnh", "📖", listOf("#TiepSucMuaThi")),
        "playground" to SubjectEntry("khoảng sân chơi rộn ràng", "🛝", emptyList()),
        "village" to SubjectEntry("vùng quê đầy nắng", "🏞️", listOf("#MuaHeXanh")),
        "city" to SubjectEntry("phố xá thân thương", "🌆", emptyList()),

        // Nature & outdoor
        "plant" to SubjectEntry("không gian xanh mát", "🌳", listOf("#MuaHeXanh")),
        "tree" to SubjectEntry("những hàng cây xanh", "🌳", listOf("#MuaHeXanh")),
        "flower" to SubjectEntry("sắc hoa rực rỡ", "🌸", emptyList()),
        "grass" to SubjectEntry("bãi cỏ xanh mướt", "🌿", listOf("#MuaHeXanh")),
        "leaf" to SubjectEntry("không khí tươi mát", "🍃", listOf("#MuaHeXanh")),
        "outdoor" to SubjectEntry("không khí ngoài trời", "🌤️", listOf("#MuaHeXanh")),
        "garden" to SubjectEntry("khu vườn xanh tươi", "🌻", listOf("#MuaHeXanh")),
        "beach" to SubjectEntry("bờ biển lộng gió", "🏖️", emptyList()),
        "mountain" to SubjectEntry("dãy núi hùng vĩ", "⛰️", listOf("#MuaHeXanh")),
        "river" to SubjectEntry("dòng sông quê hương", "🏞️", emptyList()),
        "sky" to SubjectEntry("khung trời rộng mở", "🌅", listOf("#MuaHeXanh")),
        "sunset" to SubjectEntry("hoàng hôn lung linh", "🌅", emptyList()),
        "night" to SubjectEntry("đêm hoạt động sôi nổi", "🌙", emptyList()),
        "morning" to SubjectEntry("buổi sáng đầy nắng", "🌞", emptyList()),

        // Transport
        "road" to SubjectEntry("những cung đường đầy nắng", "🚲", listOf("#MuaHeXanh")),
        "bicycle" to SubjectEntry("những vòng bánh xe tình nguyện", "🚲", listOf("#MuaHeXanh")),
        "motorcycle" to SubjectEntry("đoàn xe máy lên đường", "🏍️", listOf("#MuaHeXanh")),
        "vehicle" to SubjectEntry("hành trình lên đường", "🚗", listOf("#MuaHeXanh")),
        "bus" to SubjectEntry("chuyến xe chở yêu thương", "🚌", listOf("#MuaHeXanh")),
        "car" to SubjectEntry("đoàn xe khởi hành", "🚗", emptyList()),

        // Events
        "stage" to SubjectEntry("sân khấu rộn ràng", "🎤", emptyList()),
        "performance" to SubjectEntry("tiết mục văn nghệ ấm áp", "🎶", emptyList()),
        "music" to SubjectEntry("giai điệu của tuổi trẻ", "🎶", emptyList()),
        "dance" to SubjectEntry("điệu nhảy đầy năng lượng", "💃", emptyList()),
        "guitar" to SubjectEntry("tiếng đàn ghi-ta gắn kết", "🎸", emptyList()),
        "flag" to SubjectEntry("màu áo xanh tình nguyện", "🚩", listOf("#MuaHeXanh")),
        "banner" to SubjectEntry("băng-rôn rực rỡ", "🎌", emptyList()),
        "tent" to SubjectEntry("trại tình nguyện rộn ràng", "⛺", listOf("#MuaHeXanh")),
        "table" to SubjectEntry("bàn trao quà chu đáo", "🎁", emptyList()),
        "gift" to SubjectEntry("những phần quà yêu thương", "🎁", listOf("#UITxCongDong")),
        "balloon" to SubjectEntry("bóng bay rực rỡ sắc màu", "🎈", emptyList()),

        // Animals
        "dog" to SubjectEntry("chú cún đáng yêu", "🐶", emptyList()),
        "cat" to SubjectEntry("bé mèo dễ thương", "🐱", emptyList()),
        "animal" to SubjectEntry("các bạn nhỏ bốn chân", "🐾", emptyList())
    )

    val TitleTemplates: List<String> = listOf(
        "{program} – Khoảnh khắc cùng {team}",
        "Hành trình của {team} tại {program}",
        "{team} viết tiếp câu chuyện {program}",
        "Một ngày ý nghĩa cùng {team}",
        "{program} – Lan tỏa yêu thương cùng {team}",
        "Nhật ký {program}: {team} đã có mặt!",
        "{team} – Sắc xanh tình nguyện tại {program}",
        "Tuổi trẻ {team} cháy hết mình cùng {program}",
        "{program}: Chuyện chưa kể của {team}",
        "Khi {team} mang {program} đến gần hơn với cộng đồng",
        "{team} – Một ngày trọn vẹn tại {program}",
        "Mảnh ghép đẹp của {program} – {team}"
    )

    val OpeningSentences: List<String> = listOf(
        "Hôm nay, {team} đã có một buổi hoạt động đầy ý nghĩa trong khuôn khổ {program}.",
        "{team} vừa khép lại một ngày thật đáng nhớ tại {program}.",
        "{program} tiếp tục lăn bánh cùng {team} với rất nhiều khoảnh khắc đẹp.",
        "Một ngày nữa của {program} – một ngày nữa {team} mang yêu thương đi xa hơn.",
        "Chào cả nhà, {team} xin gửi lời chào từ điểm hoạt động {program}!",
        "Cùng nhìn lại một ngày hoạt động đáng nhớ của {team} tại {program}.",
        "{team} vừa trải qua những giờ phút khó quên trong chương trình {program}.",
        "Một buổi sáng đẹp trời, {team} đã cùng nhau góp mặt tại {program}."
    )

    val SubjectsConnectors: List<String> = listOf(
        "Trong từng khung hình là {subjects}",
        "Đập vào mắt chúng mình là {subjects}",
        "Đáng nhớ nhất hôm nay chính là {subjects}",
        "Khoảnh khắc đẹp nhất phải kể đến {subjects}",
        "{subjects} – tất cả đã tạo nên một bức tranh thật đẹp",
        "Không thể không nhắc đến {subjects}",
        "Hình ảnh khiến cả đội xúc động chính là {subjects}"
    )

    val ClosingSentences: List<String> = listOf(
        "Cảm ơn tất cả các tình nguyện viên đã đồng hành – tuổi trẻ UIT thật rạng rỡ!",
        "Hẹn gặp lại các bạn ở những điểm đến tiếp theo nhé!",
        "Hành trình vẫn còn rất dài phía trước, hãy cùng đồng hành cùng {team} nha!",
        "Tuổi trẻ UIT, chúng mình cùng tiến bước!",
        "Cảm ơn vì đã cùng {team} viết tiếp những trang đẹp của {program}.",
        "Mỗi nụ cười hôm nay đều là động lực cho chuyến đi kế tiếp.",
        "Theo dõi {team} để không bỏ lỡ những khoảnh khắc tiếp theo nhé!",
        "Đây mới chỉ là khởi đầu – còn rất nhiều điều thú vị đang chờ phía trước!"
    )

    val MiddleEmphases: List<String> = listOf(
        "Mỗi nụ cười, mỗi cái bắt tay đều là một mảnh ghép cho hành trình tình nguyện của chúng mình.",
        "Cả đội đã làm việc hết mình từ sáng sớm cho đến tận chiều muộn.",
        "Không có gì ấm áp hơn cảm giác cùng nhau làm điều ý nghĩa.",
        "Mọi nỗ lực hôm nay đều xứng đáng – cảm ơn các tình nguyện viên đã không ngại nắng mưa.",
        "Năng lượng tích cực lan tỏa khắp địa điểm hoạt động.",
        "Cảm xúc khó tả khi thấy nụ cười của bà con xen lẫn niềm vui của các bạn TNV."
    )

    val FallbackSubject = SubjectEntry(
        text = "những khoảnh khắc đáng nhớ",
        emoji = "📸",
        extraHashtags = emptyList()
    )

    val BaseHashtags: List<String> = listOf("#UIT", "#UITVolunteer", "#TinhNguyenUIT")

    val ProgramHashtagHints: Map<Regex, String> = mapOf(
        Regex("mùa hè xanh|mua he xanh", RegexOption.IGNORE_CASE) to "#MuaHeXanh",
        Regex("xuân tình nguyện|xuan tinh nguyen", RegexOption.IGNORE_CASE) to "#XuanTinhNguyen",
        Regex("tiếp sức mùa thi|tiep suc mua thi", RegexOption.IGNORE_CASE) to "#TiepSucMuaThi",
        Regex("hoa phượng đỏ|hoa phuong do", RegexOption.IGNORE_CASE) to "#HoaPhuongDo",
        Regex("kỳ nghỉ hồng|ky nghi hong", RegexOption.IGNORE_CASE) to "#KyNghiHong",
        Regex("xuân yêu thương|xuan yeu thuong", RegexOption.IGNORE_CASE) to "#XuanYeuThuong"
    )

    data class SubjectEntry(
        val text: String,
        val emoji: String,
        val extraHashtags: List<String>
    )
}
