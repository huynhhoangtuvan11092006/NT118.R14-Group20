package com.example.ielts.data.datasource

import com.example.ielts.R

data class VocabularyWord(
    val id: String,
    val topicId: String,
    val word: String,
    val phonetic: String,
    val pos: String,
    val band: Double,
    val meaningVi: String,
    val definition: String,
    val example: String,
    val exampleVi: String,
    val collocations: String,
    val synonyms: String,
    val antonyms: String
) {
    /** Từ band >= 7.0 được xếp vào nhóm "Band 7.5+" khi lọc. */
    val isAdvanced: Boolean get() = band >= 7.0
}

data class VocabularyTopic(
    val id: String,
    val title: String,
    val titleVi: String,
    val iconRes: Int,
    val accentColor: String,
    val tileColor: String
)

data class VocabularyDeck(
    val id: String,
    val title: String,
    val badge: String,
    val badgeTextColor: String,
    val badgeBgColor: String,
    val iconRes: Int,
    val iconColor: String,
    val iconTileColor: String,
    val isPhraseDeck: Boolean
)

object VocabularyDataSource {

    const val SCOPE_ALL = "all"
    const val SCOPE_TOPIC = "topic"
    const val SCOPE_DECK = "deck"

    const val DECK_WRITING = "deck_writing"
    const val DECK_IDIOMS = "deck_idioms"
    const val DECK_SIGNPOST = "deck_signpost"

    private const val TOPIC_IDIOMS = "idioms"
    private const val TOPIC_SIGNPOST = "signpost"

    val topics: List<VocabularyTopic> = listOf(
        VocabularyTopic("environment", "Environment", "Môi trường", R.drawable.ic_eco, "#16A34A", "#E6F8EE"),
        VocabularyTopic("education", "Education", "Giáo dục", R.drawable.ic_school, "#2563EB", "#E8EEFF"),
        VocabularyTopic("technology", "Technology & AI", "Công nghệ", R.drawable.ic_terminal, "#2563EB", "#E8EEFF"),
        VocabularyTopic("work", "Work & Careers", "Công việc", R.drawable.ic_work, "#B54E00", "#FFE9DC")
    )

    val decks: List<VocabularyDeck> = listOf(
        VocabularyDeck(
            DECK_WRITING, "Từ vựng ăn điểm Writing Task 2", "Band 7.5+",
            "#003EA8", "#DBE1FF", R.drawable.ic_draw, "#2563EB", "#E8EEFF", false
        ),
        VocabularyDeck(
            DECK_IDIOMS, "Idioms thường gặp Speaking", "Tự nhiên",
            "#15803D", "#DDF7E8", R.drawable.ic_mic, "#DC2626", "#FDECEC", true
        ),
        VocabularyDeck(
            DECK_SIGNPOST, "Signpost Words trong Listening", "Section 4",
            "#434655", "#E2E7FF", R.drawable.ic_headphones, "#2563EB", "#DDE5FF", true
        )
    )

    private fun w(
        topic: String, n: Int, word: String, ipa: String, pos: String, band: Double,
        vi: String, def: String, ex: String, exVi: String,
        colloc: String, syn: String, ant: String
    ) = VocabularyWord("${topic}_$n", topic, word, ipa, pos, band, vi, def, ex, exVi, colloc, syn, ant)

    val allWords: List<VocabularyWord> = listOf(
        // ===== ENVIRONMENT =====
        w("environment", 1, "deteriorate", "/dɪˈtɪə.ri.ə.reɪt/", "verb", 7.0,
            "Trở nên tồi tệ hơn, suy giảm", "to become worse",
            "The water quality has deteriorated rapidly due to industrial pollution.",
            "Chất lượng nguồn nước đã suy giảm nghiêm trọng do ô nhiễm công nghiệp.",
            "deteriorate rapidly / sharply; conditions deteriorate", "worsen, decline, degenerate", "improve, ameliorate"),
        w("environment", 2, "mitigate", "/ˈmɪt.ɪ.ɡeɪt/", "verb", 7.5,
            "Làm giảm nhẹ, xoa dịu", "to make something less harmful or serious",
            "Planting trees can mitigate the effects of climate change.",
            "Trồng cây có thể làm giảm nhẹ tác động của biến đổi khí hậu.",
            "mitigate the effects / impact / risk", "alleviate, reduce, lessen", "aggravate, worsen"),
        w("environment", 3, "biodiversity", "/ˌbaɪ.əʊ.daɪˈvɜː.sə.ti/", "noun", 7.0,
            "Đa dạng sinh học", "the variety of plant and animal life in a place",
            "Deforestation is a serious threat to global biodiversity.",
            "Phá rừng là mối đe dọa nghiêm trọng đối với đa dạng sinh học toàn cầu.",
            "loss of biodiversity; protect biodiversity", "ecological diversity", "uniformity"),
        w("environment", 4, "emission", "/ɪˈmɪʃ.ən/", "noun", 6.5,
            "Khí thải, sự phát thải", "gas or other substance sent out into the air",
            "Governments must cut carbon emissions to slow global warming.",
            "Chính phủ phải cắt giảm khí thải carbon để làm chậm sự nóng lên toàn cầu.",
            "carbon / greenhouse gas emissions; cut emissions", "discharge, release", "absorption"),
        w("environment", 5, "sustainable", "/səˈsteɪ.nə.bəl/", "adjective", 6.5,
            "Bền vững", "able to continue without damaging the environment",
            "We need to switch to more sustainable sources of energy.",
            "Chúng ta cần chuyển sang các nguồn năng lượng bền vững hơn.",
            "sustainable development / energy / growth", "viable, eco-friendly", "unsustainable, wasteful"),
        w("environment", 6, "conservation", "/ˌkɒn.səˈveɪ.ʃən/", "noun", 6.5,
            "Sự bảo tồn", "protection of nature and natural resources",
            "Wildlife conservation requires international cooperation.",
            "Bảo tồn động vật hoang dã đòi hỏi sự hợp tác quốc tế.",
            "wildlife / energy conservation; conservation efforts", "preservation, protection", "destruction, depletion"),
        w("environment", 7, "deplete", "/dɪˈpliːt/", "verb", 7.5,
            "Làm cạn kiệt", "to reduce something so there is little left",
            "Overfishing has severely depleted fish stocks in the region.",
            "Đánh bắt quá mức đã làm cạn kiệt nghiêm trọng trữ lượng cá trong khu vực.",
            "deplete resources / reserves / the ozone layer", "exhaust, use up, drain", "replenish, restore"),
        w("environment", 8, "pollutant", "/pəˈluː.tənt/", "noun", 6.5,
            "Chất gây ô nhiễm", "a substance that makes air, water or soil dirty",
            "Factories release harmful pollutants into rivers.",
            "Các nhà máy thải chất gây ô nhiễm có hại ra sông.",
            "harmful / toxic pollutants; airborne pollutants", "contaminant, toxin", "-"),
        w("environment", 9, "renewable", "/rɪˈnjuː.ə.bəl/", "adjective", 6.0,
            "Có thể tái tạo", "able to be replaced naturally, not used up",
            "Solar and wind power are renewable sources of energy.",
            "Điện mặt trời và điện gió là các nguồn năng lượng tái tạo.",
            "renewable energy / resources / sources", "replenishable, inexhaustible", "finite, non-renewable"),
        w("environment", 10, "degradation", "/ˌdeɡ.rəˈdeɪ.ʃən/", "noun", 7.5,
            "Sự suy thoái", "the process of becoming worse in quality",
            "Soil degradation reduces the amount of farmland available.",
            "Suy thoái đất làm giảm diện tích đất canh tác.",
            "environmental / soil / land degradation", "deterioration, decline", "improvement, restoration"),
        w("environment", 11, "habitat", "/ˈhæb.ɪ.tæt/", "noun", 6.5,
            "Môi trường sống", "the natural home of an animal or plant",
            "Many species are losing their natural habitat to urban expansion.",
            "Nhiều loài đang mất môi trường sống tự nhiên do đô thị mở rộng.",
            "natural habitat; habitat loss / destruction", "environment, territory", "-"),
        w("environment", 12, "eco-friendly", "/ˌiː.kəʊˈfrend.li/", "adjective", 6.0,
            "Thân thiện với môi trường", "not harmful to the environment",
            "More consumers are choosing eco-friendly products.",
            "Ngày càng nhiều người tiêu dùng chọn sản phẩm thân thiện với môi trường.",
            "eco-friendly products / packaging / transport", "green, environmentally friendly", "harmful, polluting"),

        // ===== EDUCATION =====
        w("education", 1, "curriculum", "/kəˈrɪk.jə.ləm/", "noun", 6.5,
            "Chương trình giảng dạy", "the subjects taught in a school or course",
            "The national curriculum should include practical life skills.",
            "Chương trình quốc gia nên bao gồm các kỹ năng sống thực tế.",
            "national curriculum; design / revise the curriculum", "syllabus, programme", "-"),
        w("education", 2, "literacy", "/ˈlɪt.ər.ə.si/", "noun", 6.5,
            "Khả năng đọc viết", "the ability to read and write",
            "Improving adult literacy can reduce poverty.",
            "Nâng cao khả năng đọc viết của người lớn có thể giảm nghèo.",
            "literacy rate; digital literacy; adult literacy", "reading and writing ability", "illiteracy"),
        w("education", 3, "tuition", "/tjuˈɪʃ.ən/", "noun", 6.0,
            "Học phí, sự giảng dạy", "teaching, or the money paid for it",
            "Rising tuition fees prevent many students from attending university.",
            "Học phí tăng cao khiến nhiều sinh viên không thể theo học đại học.",
            "tuition fees; private tuition", "fees, instruction", "-"),
        w("education", 4, "compulsory", "/kəmˈpʌl.sər.i/", "adjective", 6.5,
            "Bắt buộc", "required by law or rule",
            "Education is compulsory for children up to the age of sixteen.",
            "Giáo dục là bắt buộc đối với trẻ em đến mười sáu tuổi.",
            "compulsory education / subject / attendance", "mandatory, obligatory", "optional, voluntary"),
        w("education", 5, "vocational", "/vəʊˈkeɪ.ʃən.əl/", "adjective", 7.0,
            "Thuộc dạy nghề", "relating to skills needed for a particular job",
            "Vocational training prepares students directly for the workplace.",
            "Đào tạo nghề chuẩn bị cho sinh viên trực tiếp bước vào nơi làm việc.",
            "vocational training / courses / education", "occupational, technical", "academic"),
        w("education", 6, "enrol", "/ɪnˈrəʊl/", "verb", 6.0,
            "Đăng ký nhập học", "to officially join a course or school",
            "More mature students are enrolling in online degree programmes.",
            "Ngày càng nhiều học viên lớn tuổi đăng ký các chương trình đại học trực tuyến.",
            "enrol on / in a course; enrol at university", "register, sign up", "withdraw"),
        w("education", 7, "attainment", "/əˈteɪn.mənt/", "noun", 7.5,
            "Thành tích đạt được", "the achievement of a particular level or skill",
            "Educational attainment is closely linked to future income.",
            "Trình độ học vấn đạt được gắn chặt với thu nhập tương lai.",
            "educational attainment; level of attainment", "achievement, accomplishment", "failure"),
        w("education", 8, "pedagogy", "/ˈped.ə.ɡɒdʒ.i/", "noun", 7.5,
            "Phương pháp sư phạm", "the theory and practice of teaching",
            "Modern pedagogy emphasises interactive and student-centred learning.",
            "Phương pháp sư phạm hiện đại nhấn mạnh việc học tương tác, lấy học sinh làm trung tâm.",
            "modern pedagogy; teaching pedagogy", "teaching methods, instruction", "-"),
        w("education", 9, "plagiarism", "/ˈpleɪ.dʒər.ɪ.zəm/", "noun", 7.0,
            "Đạo văn", "copying someone's work and presenting it as your own",
            "Universities use software to detect plagiarism in student essays.",
            "Các trường đại học dùng phần mềm để phát hiện đạo văn trong bài luận.",
            "commit plagiarism; detect plagiarism", "copying, cheating", "originality"),
        w("education", 10, "scholarship", "/ˈskɒl.ə.ʃɪp/", "noun", 6.0,
            "Học bổng", "money given to a student to pay for education",
            "She won a full scholarship to study abroad.",
            "Cô ấy giành được học bổng toàn phần để du học.",
            "win / receive a scholarship; full scholarship", "grant, bursary", "-"),
        w("education", 11, "extracurricular", "/ˌek.strə.kəˈrɪk.jə.lər/", "adjective", 6.5,
            "Ngoại khóa", "done outside the normal school lessons",
            "Extracurricular activities help students develop teamwork skills.",
            "Hoạt động ngoại khóa giúp học sinh phát triển kỹ năng làm việc nhóm.",
            "extracurricular activities / clubs", "out-of-class, additional", "academic"),
        w("education", 12, "dropout", "/ˈdrɒp.aʊt/", "noun", 7.0,
            "Người bỏ học", "a student who leaves school before finishing",
            "Financial pressure is a major cause of university dropouts.",
            "Áp lực tài chính là nguyên nhân chính khiến sinh viên bỏ học.",
            "school dropout; dropout rate", "quitter", "graduate"),

        // ===== TECHNOLOGY =====
        w("technology", 1, "innovation", "/ˌɪn.əˈveɪ.ʃən/", "noun", 6.5,
            "Sự đổi mới, sáng kiến", "a new idea, method or device",
            "Technological innovation has transformed the way we communicate.",
            "Đổi mới công nghệ đã thay đổi cách chúng ta giao tiếp.",
            "technological innovation; drive innovation", "invention, breakthrough", "tradition"),
        w("technology", 2, "automation", "/ˌɔː.təˈmeɪ.ʃən/", "noun", 7.0,
            "Sự tự động hóa", "the use of machines to do work automatically",
            "Automation may replace many routine jobs in factories.",
            "Tự động hóa có thể thay thế nhiều công việc lặp lại trong nhà máy.",
            "factory automation; increased automation", "mechanisation", "manual labour"),
        w("technology", 3, "obsolete", "/ˈɒb.sə.liːt/", "adjective", 7.0,
            "Lỗi thời", "no longer used because something newer exists",
            "Rapid progress makes electronic devices obsolete within a few years.",
            "Tiến bộ nhanh chóng khiến thiết bị điện tử lỗi thời chỉ sau vài năm.",
            "become obsolete; obsolete technology", "outdated, outmoded", "modern, up-to-date"),
        w("technology", 4, "breakthrough", "/ˈbreɪk.θruː/", "noun", 7.0,
            "Bước đột phá", "an important discovery or achievement",
            "Scientists announced a breakthrough in cancer treatment.",
            "Các nhà khoa học công bố một bước đột phá trong điều trị ung thư.",
            "major breakthrough; make a breakthrough", "advance, discovery", "setback"),
        w("technology", 5, "cyberbullying", "/ˈsaɪ.bəˌbʊl.i.ɪŋ/", "noun", 6.5,
            "Bắt nạt trực tuyến", "using the internet to hurt or frighten people",
            "Cyberbullying can seriously damage teenagers' mental health.",
            "Bắt nạt trực tuyến có thể gây tổn hại nghiêm trọng đến sức khỏe tinh thần của thanh thiếu niên.",
            "victim of cyberbullying; tackle cyberbullying", "online harassment", "-"),
        w("technology", 6, "addictive", "/əˈdɪk.tɪv/", "adjective", 6.5,
            "Gây nghiện", "causing a strong need to keep doing something",
            "Many social media apps are designed to be highly addictive.",
            "Nhiều ứng dụng mạng xã hội được thiết kế để gây nghiện cao.",
            "highly addictive; addictive behaviour", "habit-forming, compulsive", "harmless"),
        w("technology", 7, "cutting-edge", "/ˌkʌt.ɪŋ ˈedʒ/", "adjective", 7.0,
            "Tiên tiến nhất", "very modern and advanced",
            "The hospital uses cutting-edge technology to diagnose diseases.",
            "Bệnh viện sử dụng công nghệ tiên tiến nhất để chẩn đoán bệnh.",
            "cutting-edge technology / research", "state-of-the-art, advanced", "outdated"),
        w("technology", 8, "digitise", "/ˈdɪdʒ.ɪ.taɪz/", "verb", 7.5,
            "Số hóa", "to change information into digital form",
            "Libraries are digitising their collections to reach more readers.",
            "Các thư viện đang số hóa bộ sưu tập để tiếp cận nhiều độc giả hơn.",
            "digitise records / archives", "convert to digital", "-"),
        w("technology", 9, "privacy", "/ˈprɪv.ə.si/", "noun", 6.0,
            "Quyền riêng tư", "the right to keep personal information secret",
            "Many users worry about the loss of privacy online.",
            "Nhiều người dùng lo ngại về việc mất quyền riêng tư trực tuyến.",
            "invade privacy; online privacy", "confidentiality, secrecy", "exposure"),
        w("technology", 10, "gadget", "/ˈɡædʒ.ɪt/", "noun", 6.0,
            "Thiết bị điện tử nhỏ", "a small useful electronic device",
            "Teenagers spend hours playing with the latest gadgets.",
            "Thanh thiếu niên dành hàng giờ chơi với các thiết bị mới nhất.",
            "electronic gadgets; latest gadget", "device, appliance", "-"),
        w("technology", 11, "artificial intelligence", "/ˌɑː.tɪˌfɪʃ.əl ɪnˈtel.ɪ.dʒəns/", "noun", 6.5,
            "Trí tuệ nhân tạo", "computer systems able to perform tasks needing human intelligence",
            "Artificial intelligence is increasingly used in medical diagnosis.",
            "Trí tuệ nhân tạo ngày càng được dùng trong chẩn đoán y khoa.",
            "develop / use artificial intelligence", "AI, machine intelligence", "human intelligence"),
        w("technology", 12, "reliance", "/rɪˈlaɪ.əns/", "noun", 7.0,
            "Sự phụ thuộc", "the state of depending on someone or something",
            "Over-reliance on smartphones can weaken memory and focus.",
            "Phụ thuộc quá mức vào điện thoại thông minh có thể làm yếu trí nhớ và sự tập trung.",
            "reliance on technology; heavy reliance", "dependence", "independence"),

        // ===== WORK & CAREERS =====
        w("work", 1, "workforce", "/ˈwɜːk.fɔːs/", "noun", 6.5,
            "Lực lượng lao động", "all the people who work in a company or country",
            "An ageing workforce puts pressure on pension systems.",
            "Lực lượng lao động già hóa gây áp lực lên hệ thống lương hưu.",
            "skilled workforce; ageing workforce", "labour force, employees", "-"),
        w("work", 2, "promotion", "/prəˈməʊ.ʃən/", "noun", 6.0,
            "Sự thăng chức", "a move to a higher position at work",
            "Hard work and dedication led to her promotion.",
            "Sự chăm chỉ và tận tụy đã giúp cô ấy được thăng chức.",
            "get / win a promotion; promotion prospects", "advancement, career progress", "demotion"),
        w("work", 3, "redundancy", "/rɪˈdʌn.dən.si/", "noun", 7.5,
            "Sự sa thải do dư thừa lao động", "the loss of a job because it is no longer needed",
            "Automation has led to redundancies in the manufacturing sector.",
            "Tự động hóa đã dẫn đến việc cắt giảm nhân sự trong ngành sản xuất.",
            "redundancy pay; face redundancy", "dismissal, layoff", "job security"),
        w("work", 4, "freelance", "/ˈfriː.lɑːns/", "adjective", 6.0,
            "Làm việc tự do", "working for different companies rather than one employer",
            "Many designers prefer freelance work for its flexibility.",
            "Nhiều nhà thiết kế thích làm việc tự do vì tính linh hoạt.",
            "freelance writer / work", "self-employed, independent", "salaried"),
        w("work", 5, "remuneration", "/rɪˌmjuː.nəˈreɪ.ʃən/", "noun", 7.5,
            "Thù lao, tiền công", "payment for work or services",
            "Many graduates choose jobs based on remuneration rather than passion.",
            "Nhiều sinh viên tốt nghiệp chọn việc dựa trên thù lao hơn là đam mê.",
            "high remuneration; remuneration package", "payment, salary, pay", "-"),
        w("work", 6, "recruit", "/rɪˈkruːt/", "verb", 6.5,
            "Tuyển dụng", "to find new people to work for an organisation",
            "Companies struggle to recruit qualified engineers.",
            "Các công ty gặp khó khăn trong việc tuyển dụng kỹ sư có trình độ.",
            "recruit staff / graduates", "hire, employ, take on", "dismiss, fire"),
        w("work", 7, "productivity", "/ˌprɒd.ʌkˈtɪv.ə.ti/", "noun", 6.5,
            "Năng suất", "the rate at which work is done",
            "Flexible working hours can boost employee productivity.",
            "Giờ làm việc linh hoạt có thể nâng cao năng suất của nhân viên.",
            "boost / increase productivity; labour productivity", "efficiency, output", "inefficiency"),
        w("work", 8, "burnout", "/ˈbɜːn.aʊt/", "noun", 7.0,
            "Kiệt sức vì công việc", "extreme tiredness caused by working too hard",
            "Long working hours often lead to burnout among young professionals.",
            "Giờ làm việc kéo dài thường dẫn đến kiệt sức ở những người trẻ chuyên nghiệp.",
            "suffer from burnout; avoid burnout", "exhaustion, fatigue", "energy"),
        w("work", 9, "commute", "/kəˈmjuːt/", "verb", 6.0,
            "Đi lại hằng ngày giữa nhà và nơi làm", "to travel regularly between home and work",
            "Millions of people commute to the city centre every day.",
            "Hàng triệu người đi làm vào trung tâm thành phố mỗi ngày.",
            "daily commute; commute to work", "travel to work", "-"),
        w("work", 10, "entrepreneur", "/ˌɒn.trə.prəˈnɜːr/", "noun", 7.0,
            "Doanh nhân khởi nghiệp", "a person who starts their own business",
            "Young entrepreneurs are creating new jobs through start-ups.",
            "Các doanh nhân trẻ đang tạo ra việc làm mới thông qua khởi nghiệp.",
            "successful entrepreneur; budding entrepreneur", "businessperson, founder", "employee"),
        w("work", 11, "qualification", "/ˌkwɒl.ɪ.fɪˈkeɪ.ʃən/", "noun", 6.0,
            "Bằng cấp, trình độ chuyên môn", "an exam passed or a course completed",
            "A professional qualification increases your chances of finding a job.",
            "Bằng cấp chuyên môn làm tăng cơ hội tìm được việc làm.",
            "hold a qualification; academic qualifications", "certificate, credential", "-"),
        w("work", 12, "work-life balance", "/ˌwɜːk ˈlaɪf ˌbæl.əns/", "noun", 6.5,
            "Cân bằng công việc – cuộc sống", "a good balance between job and personal life",
            "Many employees now value work-life balance above high salaries.",
            "Nhiều nhân viên nay coi trọng sự cân bằng công việc – cuộc sống hơn mức lương cao.",
            "achieve / maintain work-life balance", "life balance", "overwork"),

        // ===== IDIOMS (Speaking) =====
        w(TOPIC_IDIOMS, 1, "a blessing in disguise", "/ə ˈbles.ɪŋ ɪn dɪsˈɡaɪz/", "idiom", 7.5,
            "Trong cái rủi có cái may", "something bad that later has a good result",
            "Losing that job was a blessing in disguise because I started my own company.",
            "Mất công việc đó hóa ra là trong cái rủi có cái may vì tôi đã mở công ty riêng.",
            "Speaking Part 2: kể về một trải nghiệm bất ngờ", "a hidden advantage", "-"),
        w(TOPIC_IDIOMS, 2, "break the ice", "/breɪk ði aɪs/", "idiom", 7.0,
            "Phá vỡ sự ngượng ngùng ban đầu", "to make people feel more relaxed when they first meet",
            "He told a joke to break the ice at the meeting.",
            "Anh ấy kể một câu chuyện cười để phá tan không khí ngượng ngùng trong buổi họp.",
            "Speaking Part 1 & 3: nói về làm quen, giao tiếp", "ease the tension", "-"),
        w(TOPIC_IDIOMS, 3, "once in a blue moon", "/wʌns ɪn ə bluː muːn/", "idiom", 7.0,
            "Rất hiếm khi", "very rarely",
            "I only eat fast food once in a blue moon.",
            "Tôi chỉ ăn đồ ăn nhanh rất hiếm khi.",
            "Speaking Part 1: trả lời về tần suất (How often...?)", "very rarely, hardly ever", "all the time"),
        w(TOPIC_IDIOMS, 4, "hit the books", "/hɪt ðə bʊks/", "idiom", 7.0,
            "Học bài chăm chỉ", "to study hard",
            "I need to hit the books tonight because my exam is tomorrow.",
            "Tối nay tôi phải học bài chăm chỉ vì ngày mai có kỳ thi.",
            "Speaking Part 1: chủ đề Study", "study hard, cram", "slack off"),
        w(TOPIC_IDIOMS, 5, "under the weather", "/ˌʌn.dər ðə ˈweð.ər/", "idiom", 7.0,
            "Không khỏe, hơi mệt", "feeling slightly ill",
            "I'm feeling a bit under the weather today.",
            "Hôm nay tôi thấy hơi không khỏe.",
            "Speaking Part 1 & 2: chủ đề Health", "unwell, off-colour", "in good health"),
        w(TOPIC_IDIOMS, 6, "the best of both worlds", "/ðə best əv bəʊθ wɜːldz/", "idiom", 7.5,
            "Được lợi cả đôi đường", "the advantages of two different things at the same time",
            "Working from home gives me the best of both worlds.",
            "Làm việc tại nhà giúp tôi được lợi cả đôi đường.",
            "Speaking Part 3: so sánh hai lựa chọn", "win-win situation", "-"),
        w(TOPIC_IDIOMS, 7, "a piece of cake", "/ə piːs əv keɪk/", "idiom", 7.0,
            "Dễ như ăn kẹo", "something very easy to do",
            "The speaking test was a piece of cake for her.",
            "Bài thi nói đối với cô ấy dễ như ăn kẹo.",
            "Speaking Part 2: kể về một thử thách đã vượt qua", "very easy, a breeze", "a tough challenge"),
        w(TOPIC_IDIOMS, 8, "on the same page", "/ɒn ðə seɪm peɪdʒ/", "idiom", 7.5,
            "Cùng quan điểm, hiểu ý nhau", "having the same understanding or opinion",
            "Let's make sure we are all on the same page before we start.",
            "Hãy chắc chắn rằng tất cả chúng ta cùng hiểu như nhau trước khi bắt đầu.",
            "Speaking Part 3: chủ đề Teamwork", "in agreement", "at odds"),

        // ===== SIGNPOST WORDS (Listening) =====
        w(TOPIC_SIGNPOST, 1, "moving on to", "/ˈmuː.vɪŋ ɒn tuː/", "phrase", 6.5,
            "Chuyển sang phần tiếp theo", "used to introduce a new topic or section",
            "Moving on to the second stage, researchers measured the results.",
            "Chuyển sang giai đoạn hai, các nhà nghiên cứu đã đo lại kết quả.",
            "Listening Section 4: báo hiệu đổi chủ đề, sắp có đáp án mới", "next, turning to", "-"),
        w(TOPIC_SIGNPOST, 2, "in contrast", "/ɪn ˈkɒn.trɑːst/", "phrase", 6.5,
            "Ngược lại", "used to show a clear difference",
            "Urban areas grew quickly; in contrast, rural populations declined.",
            "Khu vực thành thị phát triển nhanh; ngược lại, dân số nông thôn giảm.",
            "Listening: báo hiệu thông tin đối lập, cẩn thận bẫy", "on the other hand, whereas", "similarly"),
        w(TOPIC_SIGNPOST, 3, "as a result", "/æz ə rɪˈzʌlt/", "phrase", 6.5,
            "Kết quả là", "used to introduce the effect of something",
            "Funding was cut. As a result, the project was cancelled.",
            "Kinh phí bị cắt. Kết quả là dự án bị hủy.",
            "Listening: đáp án thường nằm sau cụm này", "consequently, therefore", "-"),
        w(TOPIC_SIGNPOST, 4, "to sum up", "/tuː sʌm ʌp/", "phrase", 7.0,
            "Tóm lại", "used to introduce a short summary",
            "To sum up, the experiment was successful in three key areas.",
            "Tóm lại, thí nghiệm đã thành công ở ba lĩnh vực chính.",
            "Listening: báo hiệu phần kết luận", "in conclusion, overall", "-"),
        w(TOPIC_SIGNPOST, 5, "however", "/haʊˈev.ər/", "adverb", 6.5,
            "Tuy nhiên", "used to introduce a surprising or contrasting statement",
            "The plan looked perfect. However, it failed in practice.",
            "Kế hoạch trông hoàn hảo. Tuy nhiên, nó thất bại trên thực tế.",
            "Listening: người nói đổi ý, đáp án đúng thường sau 'however'", "nevertheless, yet", "-"),
        w(TOPIC_SIGNPOST, 6, "for instance", "/fɔːr ˈɪn.stəns/", "phrase", 6.5,
            "Ví dụ như", "used to give an example",
            "Many species are at risk; for instance, the Asian elephant.",
            "Nhiều loài đang bị đe dọa; ví dụ như voi châu Á.",
            "Listening: sau cụm này thường là từ cần điền", "for example, such as", "-"),
        w(TOPIC_SIGNPOST, 7, "on the other hand", "/ɒn ði ˈʌð.ər hænd/", "phrase", 6.5,
            "Mặt khác", "used to introduce a different point of view",
            "Cars are convenient; on the other hand, they cause pollution.",
            "Ô tô thì tiện lợi; mặt khác, chúng gây ô nhiễm.",
            "Listening: chuyển sang quan điểm đối lập", "conversely, alternatively", "-"),
        w(TOPIC_SIGNPOST, 8, "in addition", "/ɪn əˈdɪʃ.ən/", "phrase", 6.5,
            "Ngoài ra", "used to add another point",
            "The gym offers classes. In addition, there is a free car park.",
            "Phòng tập có các lớp học. Ngoài ra còn có bãi đỗ xe miễn phí.",
            "Listening: báo hiệu thông tin bổ sung cùng ý", "furthermore, moreover", "-")
    )

    private val wordById: Map<String, VocabularyWord> = allWords.associateBy { it.id }

    fun getWord(id: String): VocabularyWord? = wordById[id]

    fun getTopic(id: String): VocabularyTopic? = topics.find { it.id == id }

    fun getDeck(id: String): VocabularyDeck? = decks.find { it.id == id }

    fun wordsOfTopic(topicId: String): List<VocabularyWord> = allWords.filter { it.topicId == topicId }

    fun wordsOfDeck(deckId: String): List<VocabularyWord> = when (deckId) {
        DECK_WRITING -> allWords.filter { it.topicId in topics.map { t -> t.id } && it.isAdvanced }
        DECK_IDIOMS -> wordsOfTopic(TOPIC_IDIOMS)
        DECK_SIGNPOST -> wordsOfTopic(TOPIC_SIGNPOST)
        else -> emptyList()
    }

    fun deckSubtitle(deckId: String): String {
        val n = wordsOfDeck(deckId).size
        return when (deckId) {
            DECK_WRITING -> "$n từ academic • Paraphrase"
            DECK_IDIOMS -> "$n cụm từ diễn đạt tự nhiên"
            DECK_SIGNPOST -> "$n từ chuyển hướng & từ khóa"
            else -> "$n từ"
        }
    }

    /** Toàn bộ từ thuộc phạm vi học (tất cả / một chủ đề / một bộ từ). */
    fun resolveScope(type: String, id: String?): List<VocabularyWord> = when (type) {
        SCOPE_TOPIC -> id?.let { wordsOfTopic(it) } ?: emptyList()
        SCOPE_DECK -> id?.let { wordsOfDeck(it) } ?: emptyList()
        else -> allWords
    }

    fun scopeTitle(type: String, id: String?): String = when (type) {
        SCOPE_TOPIC -> getTopic(id ?: "")?.titleVi ?: "Từ vựng"
        SCOPE_DECK -> when (id) {
            DECK_WRITING -> "Writing Task 2"
            DECK_IDIOMS -> "Idioms"
            DECK_SIGNPOST -> "Signpost"
            else -> "Từ vựng"
        }
        else -> "Ôn tập tổng hợp"
    }
}

