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

    const val DECK_CAM19 = "deck_cam19"
    const val DECK_CAM18 = "deck_cam18"
    const val DECK_CAM17 = "deck_cam17"
    const val DECK_CAM16 = "deck_cam16"
    const val DECK_WRITING = "deck_writing"
    const val DECK_IDIOMS = "deck_idioms"
    const val DECK_SIGNPOST = "deck_signpost"

    private const val TOPIC_IDIOMS = "idioms"
    private const val TOPIC_SIGNPOST = "signpost"

    val topics: List<VocabularyTopic> = listOf(
        VocabularyTopic("environment", "Environment & Ecology", "Môi trường & Sinh thái", R.drawable.ic_eco, "#16A34A", "#E6F8EE"),
        VocabularyTopic("education", "Education & Academia", "Giáo dục & Học thuật", R.drawable.ic_school, "#2563EB", "#E8EEFF"),
        VocabularyTopic("technology", "Technology & AI", "Công nghệ & AI", R.drawable.ic_terminal, "#4F46E5", "#EEF2FF"),
        VocabularyTopic("work", "Work & Economy", "Nghề nghiệp & Kinh tế", R.drawable.ic_work, "#B54E00", "#FFE9DC"),
        VocabularyTopic("science_archaeology", "Science & Archaeology", "Khoa học & Khảo cổ", R.drawable.ic_auto_fix_high, "#7C3AED", "#F3E8FF"),
        VocabularyTopic("health_psychology", "Health & Psychology", "Sức khỏe & Tâm lý", R.drawable.ic_stars, "#DC2626", "#FEE2E2"),
        VocabularyTopic("urban_society", "Urban & Community", "Đô thị & Đời sống", R.drawable.ic_explore, "#0D9488", "#CCFBF1"),
        VocabularyTopic("culture_arts", "Culture & Heritage", "Văn hóa & Di sản", R.drawable.ic_menu_book, "#D97706", "#FEF3C7")
    )

    val decks: List<VocabularyDeck> = listOf(
        VocabularyDeck(
            DECK_CAM19, "Cambridge 19 Academic Vocabulary", "Cam 19",
            "#003EA8", "#DBE1FF", R.drawable.ic_menu_book, "#2563EB", "#E8EEFF", false
        ),
        VocabularyDeck(
            DECK_CAM18, "Cambridge 18 Academic Vocabulary", "Cam 18",
            "#15803D", "#DDF7E8", R.drawable.ic_menu_book, "#16A34A", "#E6F8EE", false
        ),
        VocabularyDeck(
            DECK_CAM17, "Cambridge 17 Core Academic", "Cam 17",
            "#B45309", "#FEF3C7", R.drawable.ic_menu_book, "#D97706", "#FEF3C7", false
        ),
        VocabularyDeck(
            DECK_CAM16, "Cambridge 16 Core Academic", "Cam 16",
            "#434655", "#E2E7FF", R.drawable.ic_menu_book, "#434655", "#E2E7FF", false
        ),
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
        id: String, topicId: String, word: String, phonetic: String, pos: String,
        band: Double, meaningVi: String, definition: String, example: String,
        exampleVi: String, collocations: String, synonyms: String, antonyms: String
    ) = VocabularyWord(
        id, topicId, word, phonetic, pos, band, meaningVi, definition,
        example, exampleVi, collocations, synonyms, antonyms
    )

    val allWords: List<VocabularyWord> = listOf(
        w(
            "env_deteriorate", "environment", "deteriorate", "/dɪ'tɪə.ri.ə.reɪt/", "verb",
            6.5, "Trở nên tồi tệ hơn, suy giảm", "to become progressively worse in quality or condition",
            "The water quality in the estuary has deteriorated rapidly due to industrial runoff.",
            "Chất lượng nguồn nước ở cửa sông đã suy giảm nhanh chóng do nước thải công nghiệp.",
            "deteriorate rapidly / sharply; conditions deteriorate", "worsen, decline, degenerate", "improve, ameliorate"
        ),
        w(
            "env_mitigate", "environment", "mitigate", "/'mɪt.ɪ.ɡeɪt/", "verb",
            7.5, "Làm giảm nhẹ, xoa dịu tác hại", "to make something less severe, serious, or painful",
            "Reforestation projects help mitigate the catastrophic impacts of severe flooding.",
            "Các dự án tái trồng rừng giúp giảm nhẹ tác động thảm khốc của những đợt lũ lụt nghiêm trọng.",
            "mitigate the effects / risk / impact; mitigate climate change", "alleviate, attenuate, diminish, reduce", "aggravate, exacerbate"
        ),
        w(
            "env_biodiversity", "environment", "biodiversity", "/ˌbaɪ.əʊ.daɪ'vɜː.sə.ti/", "noun",
            7.0, "Đa dạng sinh học", "the variety of plant and animal life in a particular habitat or the world",
            "The expansion of intensive agriculture threatens fragile biodiversity across wetlands.",
            "Sự mở rộng nông nghiệp thâm canh đang đe dọa sự đa dạng sinh học mong manh tại các vùng ngập nước.",
            "preserve / conserve biodiversity; loss of biodiversity", "ecological diversity, species richness", "monoculture"
        ),
        w(
            "env_deforestation", "environment", "deforestation", "/diːˌfɒr.ɪ'steɪ.ʃən/", "noun",
            6.5, "Nạn phá rừng, chặt phá rừng", "the action of clearing a wide area of trees",
            "Unregulated deforestation is accelerating soil erosion and altering local climate patterns.",
            "Nạn phá rừng không kiểm soát đang đẩy nhanh xói mòn đất và thay đổi các hình thái khí hậu cục bộ.",
            "halt deforestation; rate of deforestation; widespread deforestation", "forest clearance, logging", "afforestation, reforestation"
        ),
        w(
            "env_ecosystem", "environment", "ecosystem", "/'iː.kəʊˌsɪs.təm/", "noun",
            6.5, "Hệ sinh thái", "a biological community of interacting organisms and their physical environment",
            "Coral reefs provide vital marine ecosystems supporting over twenty-five percent of sea life.",
            "Các rạn san hô cung cấp các hệ sinh thái biển quan trọng hỗ trợ hơn 25% sinh vật biển.",
            "fragile ecosystem; marine / terrestrial ecosystem; disrupt an ecosystem", "ecological community, biome", "barren environment"
        ),
        w(
            "env_pollutant", "environment", "pollutant", "/pə'luː.tənt/", "noun",
            6.5, "Chất gây ô nhiễm", "a substance that pollutes something, especially water or the atmosphere",
            "Airborne pollutants such as fine particulate matter cause severe respiratory ailments.",
            "Các chất gây ô nhiễm không khí như bụi mịn gây ra các bệnh hô hấp nghiêm trọng.",
            "harmful / airborne pollutants; emit pollutants; filter out pollutants", "contaminant, waste substance", "purifier"
        ),
        w(
            "env_catastrophic", "environment", "catastrophic", "/ˌkæt.ə'strɒf.ɪk/", "adj",
            7.5, "Thảm khốc, tai hại nghiêm trọng", "involving or causing sudden great damage or suffering",
            "The dam burst resulted in catastrophic failure of downstream flood defense barriers.",
            "Vụ vỡ đập đã dẫn đến sự sụp đổ thảm khốc của các rào chắn phòng hộ ngập lụt hạ lưu.",
            "catastrophic consequences / damage / collapse; prove catastrophic", "disastrous, devastating, calamitous", "beneficial, harmless"
        ),
        w(
            "env_contamination", "environment", "contamination", "/kənˌtæm.ɪ'neɪ.ʃən/", "noun",
            7.0, "Sự ô nhiễm, tạp nhiễm độc hại", "the action or state of making or being made impure by polluting or poisoning",
            "Testing revealed high contamination levels in subterranean aquifer supplies.",
            "Kiểm tra cho thấy mức độ tạp nhiễm cao trong nguồn cung cấp tầng ngậm nước ngầm.",
            "soil / groundwater contamination; prevent contamination", "pollution, toxicity, infection", "purification, cleansing"
        ),
        w(
            "env_renewable", "environment", "renewable", "/rɪ'njuː.ə.bəl/", "adj",
            6.0, "Có thể tái tạo (năng lượng)", "not depleted when used; naturally replenished on a human timescale",
            "Transitioning to renewable energy generation is paramount to achieving carbon neutrality.",
            "Chuyển đổi sang sản xuất năng lượng tái tạo là điều tối quan trọng để đạt được mức trung hòa carbon.",
            "renewable energy / resources / sources; renewable electricity", "sustainable, replenishable", "non-renewable, exhaustible"
        ),
        w(
            "env_equilibrium", "environment", "equilibrium", "/ˌek.wɪ'lɪb.ri.əm/", "noun",
            8.0, "Trạng thái cân bằng sinh thái", "a state in which opposing forces or influences are balanced",
            "Predators play a fundamental role in maintaining the ecological equilibrium of woodlands.",
            "Động vật săn mồi đóng vai trò nền tảng trong việc duy trì sự cân bằng sinh thái của các vùng rừng.",
            "ecological equilibrium; maintain / restore equilibrium", "balance, parity, stability", "imbalance, instability"
        ),
        w(
            "env_depletion", "environment", "depletion", "/dɪ'pliː.ʃən/", "noun",
            7.0, "Sự cạn kiệt, suy giảm tài nguyên", "reduction in the number or quantity of something",
            "Over-extraction of groundwater leads to severe depletion of subterranean reservoirs.",
            "Khai thác quá mức nước ngầm dẫn đến sự cạn kiệt nghiêm trọng các túi trữ ngầm.",
            "resource / ozone depletion; cause depletion of", "exhaustion, reduction, drain", "replenishment, restoration"
        ),
        w(
            "env_conserve", "environment", "conserve", "/kən'sɜːv/", "verb",
            6.5, "Bảo tồn, giữ gìn", "protect something from harm or destruction",
            "stringent policies are enforced to conserve indigenous marine habitats.",
            "Các chính sách nghiêm ngặt được áp dụng để bảo tồn các sinh cảnh biển bản địa.",
            "conserve energy / wildlife / water; conserve natural resources", "preserve, protect, safeguard", "destroy, squander"
        ),
        w(
            "edu_curriculum", "education", "curriculum", "/kə'rɪk.jə.ləm/", "noun",
            6.5, "Khung chương trình giảng dạy", "the subjects comprising a course of study in a school or college",
            "The newly reformed curriculum emphasizes critical problem-solving over rote memorization.",
            "Chương trình giảng dạy vừa cải cách chú trọng giải quyết vấn đề phản biện hơn là học vẹt ghi nhớ.",
            "school curriculum; core / national curriculum; design a curriculum", "syllabus, program of study", "extracurricular activity"
        ),
        w(
            "edu_pedagogical", "education", "pedagogical", "/ˌped.ə'ɡɒdʒ.ɪ.kəl/", "adj",
            8.0, "Thuộc về phương pháp sư phạm", "relating to teaching methods and educational theory",
            "Modern pedagogical frameworks encourage collaborative inquiry rather than passive reception.",
            "Các khung phương pháp sư phạm hiện đại khuyến khích tìm hiểu tương tác thay vì tiếp thu thụ động.",
            "pedagogical methods / approaches / skills; pedagogical framework", "instructional, didactic, academic", "non-educational"
        ),
        w(
            "edu_literacy", "education", "literacy", "/'lɪt.ər.ə.si/", "noun",
            6.5, "Khả năng đọc viết, sự am hiểu", "the ability to read and write; competence in a specific area",
            "Financial literacy should be integrated alongside language literacy in primary school.",
            "Sự am hiểu tài chính nên được tích hợp song song với kỹ năng đọc viết tại trường tiểu học.",
            "adult literacy; computer / digital / financial literacy; literacy rate", "reading ability, competence, knowledge", "illiteracy"
        ),
        w(
            "edu_cognitive", "education", "cognitive", "/'kɒɡ.nə.tɪv/", "adj",
            7.5, "Thuộc về nhận thức và tư duy", "relating to the mental action or process of acquiring knowledge and understanding",
            "Bilingual upbringing significantly enhances children's cognitive flexibility and executive control.",
            "Việc nuôi dạy song ngữ nâng cao rõ rệt độ linh hoạt nhận thức và khả năng kiểm soát điều hành của trẻ.",
            "cognitive development / skills / ability / impairment", "intellectual, mental, reasoning", "intuitive, emotional"
        ),
        w(
            "edu_comprehend", "education", "comprehend", "/ˌkɒm.prɪ'hend/", "verb",
            7.0, "Thấu hiểu trọn vẹn, lĩnh hội", "grasp mentally; fully understand the nature or significance of something",
            "Early readers struggle to comprehend dense academic texts without structured scaffolds.",
            "Những người mới đọc gặp khó khăn khi lĩnh hội các văn bản học thuật cô đọng nếu thiếu sự hỗ trợ có cấu trúc.",
            "fail to comprehend; fully comprehend; comprehend the meaning", "grasp, fathom, assimilate", "misunderstand, misinterpret"
        ),
        w(
            "edu_empirical", "education", "empirical", "/ɪm'pɪr.ɪ.kəl/", "adj",
            7.5, "Dựa trên thực nghiệm, thực chứng", "based on, concerned with, or verifiable by observation or experience rather than theory",
            "The professor cited rigorous empirical evidence to validate the proposed learning model.",
            "Giáo sư đã viện dẫn bằng chứng thực nghiệm chặt chẽ để xác thực mô hình học tập được đề xuất.",
            "empirical evidence / data / study / research", "experiential, observable, factual", "theoretical, hypothetical"
        ),
        w(
            "edu_methodology", "education", "methodology", "/ˌmeθ.ə'dɒl.ə.dʒi/", "noun",
            7.0, "Hệ phương pháp luận nghiên cứu", "a system of methods used in a particular area of study or activity",
            "The research methodology adopted both quantitative surveys and qualitative focus groups.",
            "Phương pháp luận nghiên cứu đã áp dụng cả khảo sát định lượng và các nhóm tập trung định tính.",
            "research methodology; robust / sound methodology", "procedure, framework, approach", "disorder"
        ),
        w(
            "edu_proficiency", "education", "proficiency", "/prə'fɪʃ.ən.si/", "noun",
            7.0, "Mức độ thành thạo, sự tinh thông", "a high degree of competence or skill; expertise",
            "Attaining English proficiency requires persistent vocabulary acquisition and active speech.",
            "Để đạt được sự thành thạo tiếng Anh đòi hỏi sự tích lũy từ vựng bền bỉ và nói chủ động.",
            "demonstrate proficiency; language / technical proficiency", "mastery, competence, expertise", "incompetence, deficiency"
        ),
        w(
            "edu_syllabus", "education", "syllabus", "/'sɪl.ə.bəs/", "noun",
            6.5, "Đề cương môn học, giáo trình", "an outline of the subjects in a course of study or teaching",
            "Students should consult the course syllabus for assignment deadlines and exam formats.",
            "Sinh viên nên tham khảo đề cương môn học để biết hạn nộp bài tập và định dạng bài thi.",
            "course syllabus; on the syllabus; cover the syllabus", "course outline, prospectus", "free study"
        ),
        w(
            "edu_intellectual", "education", "intellectual", "/ˌɪn.tə'lek.tʃu.əl/", "adj",
            7.0, "Thuộc về trí tuệ, tri thức", "relating to the intellect and the capacity for higher mental thought",
            "Universities should foster intellectual curiosity rather than merely training for exams.",
            "Các trường đại học nên bồi dưỡng trí tò mò tri thức thay vì chỉ đơn thuần luyện thi.",
            "intellectual property; intellectual curiosity / development", "scholarly, academic, cerebral", "physical, anti-intellectual"
        ),
        w(
            "edu_retention", "education", "retention", "/rɪ'ten.ʃən/", "noun",
            7.5, "Khả năng ghi nhớ, lưu giữ kiến thức", "the continued possession, use, or control of something; memory retention",
            "Spaced repetition intervals drastically elevate long-term vocabulary retention rates.",
            "Các chu kỳ lặp lại ngắt quãng làm tăng vượt trội tỷ lệ lưu giữ từ vựng dài hạn.",
            "knowledge / memory retention; rate of retention", "memorization, preservation", "forgetting, loss"
        ),
        w(
            "edu_rote_learning", "education", "rote-learning", "/'rəʊt ˌlɜː.nɪŋ/", "noun",
            7.0, "Học vẹt, học thuộc lòng thụ động", "the memorization of information based on repetition, without full understanding",
            "Educators caution that rote-learning fails to prepare pupils for real-world analytical problems.",
            "Các nhà giáo dục cảnh báo rằng việc học vẹt không thể chuẩn bị cho học sinh giải quyết vấn đề phân tích thực tế.",
            "rely on rote-learning; discouraged rote-learning", "mechanical memorization, cramming", "deep understanding, conceptual learning"
        ),
        w(
            "tech_algorithm", "technology", "algorithm", "/'æl.ɡə.rɪ.ðəm/", "noun",
            6.5, "Thuật toán, quy trình tính toán", "a process or set of rules to be followed in calculations or problem-solving by a computer",
            "Search engines optimize complex recommendation algorithms to personalize user feeds.",
            "Các công cụ tìm kiếm tối ưu hóa những thuật toán gợi ý phức tạp để cá nhân hóa nguồn cấp dữ liệu người dùng.",
            "computer / search algorithm; sophisticated algorithm; execute an algorithm", "computational rule, formula", "randomness"
        ),
        w(
            "tech_automation", "technology", "automation", "/ˌɔː.tə'meɪ.ʃən/", "noun",
            7.0, "Tự động hóa", "the use of largely automatic equipment in a system of manufacturing or other processes",
            "Industrial automation has dramatically accelerated production speeds while reducing manual labor.",
            "Tự động hóa công nghiệp đã đẩy nhanh đáng kể tốc độ sản xuất đồng thời giảm bớt lao động thủ công.",
            "workplace automation; widespread automation; process automation", "mechanization, computerization", "manual operation"
        ),
        w(
            "tech_breakthrough", "technology", "breakthrough", "/'breɪk.θruː/", "noun",
            7.0, "Bước đột phá mang tính cách mạng", "a sudden, dramatic, and important discovery or development",
            "Neural networks represent a monumental breakthrough in natural language processing.",
            "Mạng nơ-ron đại diện cho một bước đột phá vĩ đại trong xử lý ngôn ngữ tự nhiên.",
            "technological / scientific breakthrough; make a major breakthrough", "advancement, leap forward, milestone", "setback, stagnation"
        ),
        w(
            "tech_cybersecurity", "technology", "cybersecurity", "/'saɪ.bə.sɪˌkjʊə.rə.ti/", "noun",
            7.0, "An ninh mạng, bảo mật thông tin", "the state of being protected against the criminal or unauthorized use of electronic data",
            "Financial institutions invest billions into cybersecurity infrastructure to thwart ransomware.",
            "Các định chế tài chính đầu tư hàng tỷ đô la vào cơ sở hạ tầng an ninh mạng để ngăn chặn mã độc tống tiền.",
            "cybersecurity breach / threat / protocol; robust cybersecurity", "information security, digital defense", "cyber vulnerability"
        ),
        w(
            "tech_ubiquitous", "technology", "ubiquitous", "/juː'bɪk.wɪ.təs/", "adj",
            8.0, "Phổ biến khắp nơi, có mặt khắp chốn", "present, appearing, or found everywhere",
            "Smartphone technology has grown so ubiquitous that life without it is virtually unimaginable.",
            "Công nghệ điện thoại thông minh đã trở nên phổ biến khắp nơi đến mức cuộc sống thiếu nó gần như không tưởng.",
            "become ubiquitous; ubiquitous computing / presence", "omnipresent, pervasive, universal", "rare, scarce, localized"
        ),
        w(
            "tech_cutting_edge", "technology", "cutting-edge", "/ˌkʌt.ɪŋ'edʒ/", "adj",
            7.0, "Tối tân, tiên tiến hàng đầu", "highly advanced; innovative or pioneering",
            "The laboratory is equipped with cutting-edge microchips capable of quantum simulations.",
            "Phòng thí nghiệm được trang bị những vi mạch tối tân có khả năng mô phỏng lượng tử.",
            "cutting-edge technology / research / equipment / design", "state-of-the-art, avant-garde, ultra-modern", "obsolete, antiquated"
        ),
        w(
            "tech_simulate", "technology", "simulate", "/'sɪm.jə.leɪt/", "verb",
            7.0, "Mô phỏng (hệ thống, hiện tượng)", "imitate the appearance or character of; produce a computer model of",
            "Flight simulators accurately simulate emergency landings in unpredictable weather.",
            "Hệ thống mô phỏng chuyến bay tái hiện chuẩn xác các lần hạ cánh khẩn cấp trong thời tiết bất thường.",
            "simulate conditions / flight / environment; computer-simulated", "reproduce, replicate, emulate", "actualize"
        ),
        w(
            "tech_biometrics", "technology", "biometrics", "/ˌbaɪ.əʊ'met.rɪks/", "noun",
            7.5, "Công nghệ sinh trắc học", "the measurement and analysis of unique physical and behavioral human characteristics",
            "Airports deploy biometrics such as iris scanning to streamline passenger immigration.",
            "Các sân bay triển khai sinh trắc học như quét mống mắt để hợp lý hóa thủ tục nhập cảnh cho hành khách.",
            "biometrics data / authentication / verification / scanner", "biological recognition, facial verification", "password authentication"
        ),
        w(
            "tech_autonomous", "technology", "autonomous", "/ɔː'tɒn.ə.məs/", "adj",
            7.5, "Tự hành, tự chủ không cần con người", "acting independently or having the freedom to do so without external control",
            "Engineers are rigorously testing autonomous delivery drones in congested urban sectors.",
            "Các kỹ sư đang kiểm thử nghiêm ngặt máy bay không người lái giao hàng tự hành tại các khu đô thị đông đúc.",
            "autonomous vehicle / robot / system / flight", "self-governing, self-driving, independent", "dependent, manually controlled"
        ),
        w(
            "tech_innovative", "technology", "innovative", "/'ɪn.ə.veɪ.tɪv/", "adj",
            6.5, "Đổi mới sáng tạo, mang tính cách tân", "featuring new methods; advanced and original",
            "The startup introduced an innovative method to filter microplastics from domestic tap water.",
            "Công ty khởi nghiệp đã giới thiệu một phương pháp đổi mới sáng tạo để lọc vi nhựa khỏi nước máy sinh hoạt.",
            "innovative approach / solution / design / technology", "inventive, trailblazing, groundbreaking", "traditional, uninspired"
        ),
        w(
            "tech_virtual", "technology", "virtual", "/'vɜː.tʃu.əl/", "adj",
            6.0, "Ảo, mô phỏng qua máy tính", "not physically existing as such but made by software to appear to do so",
            "Medical students practice complex operations inside a virtual reality environment.",
            "Sinh viên y khoa thực hành các ca mổ phức tạp bên trong môi trường thực tế ảo.",
            "virtual reality / assistant / classroom / meeting", "simulated, computer-generated", "physical, real-world"
        ),
        w(
            "tech_computation", "technology", "computation", "/ˌkɒm.pjʊ'teɪ.ʃən/", "noun",
            7.5, "Sự tính toán điện toán số lượng lớn", "the use of computers, especially as a system or process; calculation",
            "Quantum computation promises to solve mathematical equations once deemed insurmountable.",
            "Điện toán lượng tử hứa hẹn giải quyết những phương trình toán học từng bị coi là không thể vượt qua.",
            "perform a computation; quantum / cloud computation; computational power", "calculation, data processing", "estimation"
        ),
        w(
            "work_remuneration", "work", "remuneration", "/rɪˌmjuː.nər'eɪ.ʃən/", "noun",
            8.0, "Thù lao, tiền công tương xứng", "money paid for work or a service; salary and financial compensation",
            "Executives demand fair remuneration packages that reflect corporate profitability.",
            "Các nhà quản lý điều hành yêu cầu các gói thù lao công bằng phản ánh đúng lợi nhuận doanh nghiệp.",
            "financial remuneration; remuneration package; commensurate with remuneration", "compensation, salary, earnings, reward", "underpayment"
        ),
        w(
            "work_hierarchy", "work", "hierarchy", "/'haɪə.rɑː.ki/", "noun",
            7.5, "Hệ thống cấp bậc thứ bậc trong tổ chức", "a system or organization in which people or groups are ranked one above the other according to status",
            "Modern tech firms have flattened the corporate hierarchy to foster peer-level communication.",
            "Các công ty công nghệ hiện đại đã tinh giản thứ bậc doanh nghiệp để thúc đẩy giao tiếp ngang hàng.",
            "corporate / organizational hierarchy; strict hierarchy; climb the hierarchy", "pecking order, chain of command, ranking", "egalitarianism"
        ),
        w(
            "work_productivity", "work", "productivity", "/ˌprɒd.ʌk'tɪv.ə.ti/", "noun",
            6.5, "Năng suất làm việc", "the effectiveness of productive effort, especially in industry, as measured in terms of the rate of output per unit of input",
            "Flexible work-from-home options have been correlated with elevated employee productivity.",
            "Lựa chọn làm việc tại nhà linh hoạt có mối tương quan với việc gia tăng năng suất của nhân viên.",
            "boost / increase / improve productivity; worker / labor productivity", "output, efficiency, yield", "inefficiency, sluggishness"
        ),
        w(
            "work_collaboration", "work", "collaboration", "/kəˌlæb.ə'reɪ.ʃən/", "noun",
            6.5, "Sự cộng tác, hợp tác liên phòng ban", "the action of working with someone to produce or create something",
            "Cross-functional collaboration between marketing and engineering drives agile delivery.",
            "Sự cộng tác liên phòng ban giữa tiếp thị và kỹ thuật thúc đẩy quá trình chuyển giao linh hoạt.",
            "close collaboration; in collaboration with; foster collaboration", "cooperation, teamwork, partnership", "isolation, competition"
        ),
        w(
            "work_entrepreneur", "work", "entrepreneur", "/ˌɒn.trə.prə'nɜːr/", "noun",
            7.0, "Doanh nhân khởi nghiệp", "a person who sets up a business or businesses, taking on financial risks in the hope of profit",
            "Venture capital funds provide seed financing to ambitious entrepreneurs in clean energy.",
            "Các quỹ đầu tư mạo hiểm cung cấp vốn hạt giống cho những doanh nhân khởi nghiệp đầy tham vọng về năng lượng sạch.",
            "budding / aspiring entrepreneur; successful entrepreneur", "business founder, innovator, capitalist", "salaried employee"
        ),
        w(
            "work_turnover", "work", "turnover", "/'tɜːnˌəʊ.vər/", "noun",
            7.0, "Tỷ lệ luân chuyển lao động; doanh thu", "the rate at which employees leave a workforce and are replaced; total annual sales volume",
            "Poor office culture invariably triggers high employee turnover and recruitment expenses.",
            "Văn hóa công sở kém chắc chắn dẫn đến tỷ lệ luân chuyển nhân sự cao và chi phí tuyển dụng tốn kém.",
            "staff / employee turnover; annual turnover; reduce turnover", "attrition, personnel churn", "staff retention"
        ),
        w(
            "work_incentive", "work", "incentive", "/ɪn'sen.tɪv/", "noun",
            7.0, "Sự khuyến khích, động lực kinh tế", "a thing that motivates or encourages one to do something; payment or concession",
            "Performance bonuses serve as strong monetary incentives for hitting quarterly targets.",
            "Tiền thưởng hiệu suất đóng vai trò là động lực kinh tế mạnh mẽ để hoàn thành chỉ tiêu theo quý.",
            "financial / tax incentive; offer / create an incentive", "stimulus, inducement, motivation", "deterrent, disincentive"
        ),
        w(
            "work_balance", "work", "work-life balance", "/ˌwɜːk laɪf 'bæl.əns/", "noun",
            6.5, "Sự cân bằng giữa công việc và đời sống", "the division of one's time and focus between working and family or leisure activities",
            "Younger generations prioritize work-life balance over overtime pay when evaluating jobs.",
            "Các thế hệ trẻ ưu tiên sự cân bằng giữa công việc và đời sống hơn là lương làm thêm giờ khi đánh giá công việc.",
            "maintain / achieve a good work-life balance; healthy work-life balance", "lifestyle equilibrium", "workaholism, burnout"
        ),
        w(
            "work_competency", "work", "competency", "/'kɒm.pɪ.tən.si/", "noun",
            7.5, "Năng lực chuyên môn cốt lõi", "the ability to do something successfully or efficiently; a skill or knowledge required for a role",
            "The interview rubric evaluates core competencies such as conflict resolution and leadership.",
            "Khung chấm điểm phỏng vấn đánh giá các năng lực cốt lõi như giải quyết xung đột và kỹ năng lãnh đạo.",
            "core competency; demonstrate competency; competency framework", "capability, aptitude, proficiency", "incompetence, deficiency"
        ),
        w(
            "work_freelance", "work", "freelance", "/'friː.lɑːns/", "adj",
            6.5, "Làm nghề tự do không ràng buộc", "working for different companies at different times rather than being permanently employed",
            "The digital nomad economy allows graphic designers to pursue freelance contracts worldwide.",
            "Nền kinh tế du mục kỹ thuật số cho phép các nhà thiết kế đồ họa theo đuổi các hợp đồng làm việc tự do trên toàn cầu.",
            "freelance worker / journalist / consultant; work freelance", "self-employed, independent", "tenured, full-time employee"
        ),
        w(
            "work_supervision", "work", "supervision", "/ˌsuː.pə'vɪʒ.ən/", "noun",
            6.5, "Sự giám sát quản lý công việc", "the act or process of watching and directing someone or a task",
            "New apprentices operate heavy factory machinery only under constant expert supervision.",
            "Các học việc mới chỉ vận hành máy móc nhà máy hạng nặng dưới sự giám sát liên tục của chuyên gia.",
            "under direct supervision; close / minimal supervision", "oversight, surveillance, direction", "autonomy, neglect"
        ),
        w(
            "work_promotion", "work", "promotion", "/prə'məʊ.ʃən/", "noun",
            6.0, "Sự thăng chức, đề bạt công tác", "the action of promoting someone to a higher position or rank",
            "Consistent high performance on major projects fast-tracked her promotion to senior director.",
            "Hiệu suất làm việc xuất sắc liên tục trong các dự án lớn đã giúp cô được thăng chức thần tốc lên vị trí giám đốc cấp cao.",
            "earn / gain a promotion; promotion prospects; merit-based promotion", "advancement, elevation, upgrade", "demotion"
        ),
        w(
            "sci_archaeological", "science_archaeology", "archaeological", "/ˌɑː.ki.ə'lɒdʒ.ɪ.kəl/", "adj",
            7.5, "Thuộc về khảo cổ học", "relating to the study of human history and prehistory through the excavation of sites and analysis of artifacts",
            "Archaeological excavations at the Indus site unearthed intricately paved drainage networks.",
            "Các cuộc khai quật khảo cổ tại địa điểm Indus đã làm lộ diện các mạng lưới thoát nước được lát gạch tinh xảo.",
            "archaeological site / excavation / dig / evidence / survey", "antiquarian, historical excavation", "contemporary"
        ),
        w(
            "sci_artifact", "science_archaeology", "artifact", "/'ɑː.tɪ.fækt/", "noun",
            7.0, "Cổ vật, hiện vật lịch sử", "an object made by a human being, typically one of cultural or historical interest",
            "Priceless bronze artifacts were cleaned and catalogued for display in the national gallery.",
            "Những cổ vật bằng đồng vô giá đã được làm sạch và phân loại danh mục để trưng bày tại bảo tàng quốc gia.",
            "ancient / historical / prehistoric artifact; unearth an artifact", "relic, heirloom, antique", "natural formation"
        ),
        w(
            "sci_excavate", "science_archaeology", "excavate", "/'ek.skə.veɪt/", "verb",
            7.5, "Khai quật, đào bới tìm vết tích", "make a hole or channel by digging; unearth something through excavation",
            "Palaeontologists took three months to excavate the fossilized dinosaur pelvic bone.",
            "Các nhà cổ sinh vật học đã mất ba tháng để khai quật xương chậu khủng long hóa thạch.",
            "excavate a site / tomb / trench; carefully excavate", "unearth, dig up, disinter", "bury, entomb"
        ),
        w(
            "sci_hypothesis", "science_archaeology", "hypothesis", "/haɪ'pɒθ.ə.sɪs/", "noun",
            7.5, "Giả thuyết khoa học", "a supposition or proposed explanation made on the basis of limited evidence as a starting point for further investigation",
            "The experimental findings corroborated the lead researcher's initial hypothesis.",
            "Các kết quả thực nghiệm đã chứng minh giả thuyết ban đầu của nhà nghiên cứu chính.",
            "test / formulate / confirm a hypothesis; working hypothesis", "theory, conjecture, proposition", "proven fact"
        ),
        w(
            "sci_prehistoric", "science_archaeology", "prehistoric", "/ˌpriː.hɪ'stɒr.ɪk/", "adj",
            7.0, "Thời tiền sử (trước khi có sử liệu chữ viết)", "relating to the period before written records",
            "Deep inside the cave, scientists documented prehistoric charcoal paintings of woolly mammoths.",
            "Sâu bên trong hang động, các nhà khoa học đã ghi nhận các bức tranh than củi thời tiền sử vẽ voi ma mút.",
            "prehistoric times / era / art / creature / settlement", "primeval, ancient, primitive", "modern, recorded history"
        ),
        w(
            "sci_sediment", "science_archaeology", "sediment", "/'sed.ɪ.mənt/", "noun",
            7.5, "Trầm tích lắng đọng", "matter that settles to the bottom of a liquid; dregs; geological layers deposited by water or wind",
            "Core samples from lake sediment preserve microscopic pollen records spanning thousands of years.",
            "Các mẫu lõi từ trầm tích hồ lưu giữ những hạt phấn hoa siêu nhỏ trải dài hàng nghìn năm.",
            "ocean / lake sediment; thick layers of sediment; sediment deposition", "deposit, silt, sludge, alluvium", "clear liquid"
        ),
        w(
            "sci_specimen", "science_archaeology", "specimen", "/'spes.ə.mɪn/", "noun",
            7.0, "Mẫu vật thí nghiệm, mẫu tiêu bản", "an individual animal, plant, piece of a mineral, etc. used as an example of its species or type for scientific study",
            "The entomologist carefully mounted the rare butterfly specimen under microscope glass.",
            "Nhà côn trùng học cẩn thận gắn mẫu tiêu bản bướm quý hiếm dưới kính hiển vi.",
            "biological specimen; collect / examine a specimen", "sample, example, exhibit", "mass"
        ),
        w(
            "sci_civilization", "science_archaeology", "civilization", "/ˌsɪv.əl.aɪ'zeɪ.ʃən/", "noun",
            6.5, "Nền văn minh nhân loại", "the stage of human social and cultural development and organization that is considered most advanced",
            "Severe multi-decade droughts triggered the eventual collapse of the Maya civilization.",
            "Những đợt hạn hán nghiêm trọng kéo dài nhiều thập kỷ đã gây ra sự sụp đổ của nền văn minh Maya.",
            "ancient / modern civilization; cradle of civilization; rise of civilization", "society, culture, human community", "barbarism, savagery"
        ),
        w(
            "sci_durability", "science_archaeology", "durability", "/ˌdjʊə.rə'bɪl.ə.ti/", "noun",
            7.5, "Độ bền bỉ, tính chịu lực lâu bền", "the ability to withstand wear, pressure, or damage over a long period",
            "The astonishing durability of ancient Roman concrete stems from volcanic ash hydration.",
            "Độ bền kinh ngạc của bê tông La Mã cổ đại bắt nguồn từ phản ứng hydrat hóa tro núi lửa.",
            "remarkable durability; test the durability; durability and strength", "endurance, longevity, resilience", "fragility, weakness"
        ),
        w(
            "sci_radiocarbon", "science_archaeology", "radiocarbon", "/ˌreɪ.di.əʊ'kɑː.bən/", "noun",
            8.0, "Đồng vị phóng xạ cacbon (định tuổi cổ vật)", "a radioactive isotope of carbon used in dating organic materials",
            "Radiocarbon dating firmly positioned the wooden burial ship in the seventh century.",
            "Định tuổi bằng đồng vị carbon phóng xạ đã xác định chuẩn xác con thuyền an táng bằng gỗ thuộc thế kỷ thứ 7.",
            "radiocarbon dating; radiocarbon analysis", "carbon-14 dating", "relative dating"
        ),
        w(
            "sci_fossilize", "science_archaeology", "fossilize", "/'fɒs.əl.aɪz/", "verb",
            7.5, "Hóa thạch, biến thành đá", "preserve an animal or plant of a past geological age so that they become fossil",
            "Tree resin must be subjected to geological heat and pressure over millions of years to fossilize into amber.",
            "Nhựa cây phải trải qua nhiệt độ và áp suất địa chất hàng triệu năm để hóa thạch thành hổ phách.",
            "fossilized remains; fossilize over time", "petrify, mineralize", "decompose, decay"
        ),
        w(
            "sci_chronology", "science_archaeology", "chronology", "/krə'nɒl.ə.dʒi/", "noun",
            8.0, "Niên biểu, trật tự thời gian diễn tiến", "the arrangement of events or dates in the order of their occurrence",
            "Historians established a precise chronology of events leading to the palace fire.",
            "Các nhà sử học đã thiết lập một niên biểu chuẩn xác các sự kiện dẫn đến vụ cháy cung điện.",
            "historical chronology; chronological order; accurate chronology", "timeline, order of events, sequence", "anachronism"
        ),
        w(
            "hea_sedentary", "health_psychology", "sedentary", "/'sed.ən.tər.i/", "adj",
            7.5, "Ít vận động, ngồi nhiều thụ động", "tending to spend much time seated; somewhat inactive",
            "Adopting a sedentary lifestyle dramatically increases the probability of cardiovascular disease.",
            "Thói quen lối sống ít vận động làm tăng vọt xác suất mắc bệnh tim mạch.",
            "sedentary lifestyle / job / habit; sedentary behavior", "inactive, seated, desk-bound", "active, energetic"
        ),
        w(
            "hea_psychological", "health_psychology", "psychological", "/ˌsaɪ.kə'lɒdʒ.ɪ.kəl/", "adj",
            6.5, "Thuộc về tâm lý học và tinh thần", "relating to the mental and emotional state of a person",
            "Chronic sleep deprivation causes severe psychological distress and reduced cognitive performance.",
            "Thiếu ngủ kinh niên gây ra căng thẳng tâm lý nghiêm trọng và giảm sút hiệu năng nhận thức.",
            "psychological wellbeing / distress / evaluation / trauma", "mental, emotional, cerebral", "physiological, somatic"
        ),
        w(
            "hea_wellbeing", "health_psychology", "wellbeing", "/ˌwel'biː.ɪŋ/", "noun",
            7.0, "Sự khỏe khoắn toàn diện cả thể chất lẫn tinh thần", "the state of being comfortable, healthy, or happy",
            "Access to green parks and recreational corridors is indispensable for urban mental wellbeing.",
            "Tiếp cận với công viên cây xanh và các hành lang giải trí là điều không thể thiếu cho sức khỏe tinh thần đô thị.",
            "physical / emotional / general wellbeing; promote wellbeing", "welfare, health, happiness, prosperity", "misery, illness"
        ),
        w(
            "hea_epidemic", "health_psychology", "epidemic", "/ˌep.ɪ'dem.ɪk/", "noun",
            7.0, "Dịch bệnh bùng phát diện rộng", "a widespread occurrence of an infectious disease in a community at a particular time",
            "Health authorities instituted nationwide quarantine measures to curb the viral epidemic.",
            "Các cơ quan y tế đã ban hành các biện pháp cách ly toàn quốc để kiềm chế dịch bệnh do virus.",
            "curb an epidemic; epidemic proportions; obesity / flu epidemic", "outbreak, plague, pandemic", "endemic containment"
        ),
        w(
            "hea_therapeutic", "health_psychology", "therapeutic", "/ˌθer.ə'pjuː.tɪk/", "adj",
            7.5, "Có tính chất trị liệu, chữa lành", "relating to the healing of disease; having a good effect on body or mind; contributing to sense of wellbeing",
            "Art therapy has shown remarkable therapeutic benefits for individuals coping with trauma.",
            "Trị liệu nghệ thuật đã cho thấy những lợi ích chữa lành rõ rệt đối với những người đang đối phó với tổn thương tâm lý.",
            "therapeutic effect / intervention / benefit; therapeutic massage", "healing, curative, remedial, restorative", "harmful, toxic"
        ),
        w(
            "hea_cognitive_bias", "health_psychology", "cognitive bias", "/'kɒɡ.nə.tɪv ˈbaɪ.əs/", "noun",
            8.0, "Thiên kiến nhận thức, sai lệch tư duy", "a systematic pattern of deviation from norm or rationality in judgment",
            "Confirmation bias is a widespread cognitive bias where individuals ignore counter-evidence.",
            "Thiên kiến xác nhận là một dạng sai lệch tư duy phổ biến mà ở đó mọi người phớt lờ bằng chứng trái ngược.",
            "fall victim to cognitive bias; subconscious cognitive bias", "prejudice, subjective distortion", "objective rationality"
        ),
        w(
            "hea_insomnia", "health_psychology", "insomnia", "/ɪn'sɒm.ni.ə/", "noun",
            7.0, "Chứng mất ngủ kinh niên", "habitual sleeplessness; inability to sleep",
            "Excessive evening screen exposure disrupts melatonin synthesis and induces acute insomnia.",
            "Việc tiếp xúc màn hình quá mức vào buổi tối làm gián đoạn tổng hợp melatonin và gây ra chứng mất ngủ cấp tính.",
            "suffer from insomnia; chronic / acute insomnia; treat insomnia", "sleeplessness, wakefulness", "sound sleep"
        ),
        w(
            "hea_perception", "health_psychology", "perception", "/pə'sep.ʃən/", "noun",
            7.0, "Sự tri giác, cảm nhận chủ quan", "the ability to see, hear, or become aware of something through the senses; a belief or opinion",
            "Sensory illusions demonstrate how easily visual perception can be deceived by optical cues.",
            "Các ảo giác thị giác chứng minh cách mà tri giác thị giác có thể dễ dàng bị đánh lừa bởi các dấu hiệu quang học.",
            "public perception; sensory perception; perception of reality", "discernment, understanding, impression", "unawareness, blindness"
        ),
        w(
            "hea_metabolism", "health_psychology", "metabolism", "/mə'tæb.əl.ɪ.zəm/", "noun",
            7.0, "Sự trao đổi chất trong cơ thể", "the chemical processes that occur within a living organism in order to maintain life",
            "Regular aerobic exercise accelerates basal metabolism and improves insulin sensitivity.",
            "Tập thể dục nhịp điệu đều đặn giúp đẩy nhanh quá trình trao đổi chất cơ bản và cải thiện độ nhạy insulin.",
            "fast / slow metabolism; boost metabolism; basal metabolic rate", "biochemical process", "stasis"
        ),
        w(
            "hea_rehabilitation", "health_psychology", "rehabilitation", "/ˌriː.həˌbɪl.ɪ'teɪ.ʃən/", "noun",
            7.5, "Sự phục hồi chức năng thể chất", "the action of restoring someone to health or normal life through training and therapy",
            "Post-surgery knee rehabilitation requires rigorous physiotherapeutic exercises.",
            "Phục hồi chức năng khớp gối sau phẫu thuật đòi hỏi các bài tập vật lý trị liệu nghiêm ngặt.",
            "undergo rehabilitation; physical / cardiac rehabilitation", "recovery, convalescence, recuperation", "relapse, decline"
        ),
        w(
            "hea_resilience", "health_psychology", "resilience", "/rɪ'zɪl.jəns/", "noun",
            7.5, "Khả năng phục hồi, sự kiên cường", "the capacity to withstand or to recover quickly from difficulties; toughness",
            "Psychological counseling bolsters emotional resilience in students under intense academic stress.",
            "Tư vấn tâm lý giúp củng cố sự kiên cường cảm xúc ở sinh viên đang chịu áp lực học tập căng thẳng.",
            "emotional resilience; build / foster resilience; remarkable resilience", "grit, stamina, fortitude, buoyancy", "fragility, weakness"
        ),
        w(
            "hea_vulnerability", "health_psychology", "vulnerability", "/ˌvʌl.nər.ə'bɪl.ə.ti/", "noun",
            7.5, "Tính dễ bị tổn thương", "the quality or state of being exposed to the possibility of being attacked or harmed, either physically or emotionally",
            "Infants and the elderly experience heightened biological vulnerability during winter heatwaves.",
            "Trẻ sơ sinh và người cao tuổi chịu tính dễ bị tổn thương sinh học cao hơn trong các đợt biến đổi nhiệt mùa đông.",
            "expose vulnerability; heighten vulnerability; economic / emotional vulnerability", "susceptibility, defenselessness", "invulnerability, immunity"
        ),
        w(
            "urb_urbanization", "urban_society", "urbanization", "/ˌɜː.bən.aɪ'zeɪ.ʃən/", "noun",
            7.0, "Quá trình đô thị hóa", "the process of making an area more urban; the gradual increase in the proportion of people living in urban areas",
            "Rapid urbanization has put unprecedented strain on municipal sewage and transport grids.",
            "Đô thị hóa nhanh chóng đã đặt áp lực chưa từng có lên hệ thống thoát nước và giao thông của thành phố.",
            "rapid / unchecked urbanization; rate of urbanization; urbanize", "city growth, urban sprawl", "ruralization, depopulation"
        ),
        w(
            "urb_infrastructure", "urban_society", "infrastructure", "/'ɪn.frəˌstrʌk.tʃər/", "noun",
            7.0, "Cơ sở hạ tầng cơ bản", "the basic physical and organizational structures and facilities needed for the operation of a society or enterprise",
            "Upgrading public transit infrastructure is crucial to curbing reliance on personal automobiles.",
            "Nâng cấp cơ sở hạ tầng giao thông công cộng là điều thiết yếu để hạn chế sự phụ thuộc vào ô tô cá nhân.",
            "transport / communication / green infrastructure; invest in infrastructure", "framework, foundational systems, utilities", "superstructure"
        ),
        w(
            "urb_congestion", "urban_society", "congestion", "/kən'dʒes.tʃən/", "noun",
            6.5, "Sự tắc nghẽn, kẹt xe nghiêm trọng", "the state of being extremely crowded and blocked with traffic or people",
            "The implementation of congestion pricing drastically halved peak-hour delays downtown.",
            "Việc áp dụng thu phí chống ùn tắc đã giảm đi một nửa sự chậm trễ trong giờ cao điểm ở trung tâm.",
            "traffic congestion; alleviate / ease congestion; congestion charge", "gridlock, bottleneck, overcrowding", "clear passage, free flow"
        ),
        w(
            "urb_demographics", "urban_society", "demographics", "/ˌdem.ə'ɡræf.ɪks/", "noun",
            7.5, "Số liệu nhân khẩu học, cơ cấu dân số", "statistical data relating to the population and particular groups within it",
            "Shifting demographics reveal an aging population necessitating accessible city architecture.",
            "Sự thay đổi về nhân khẩu học cho thấy dân số già hóa đòi hỏi kiến trúc thành phố dễ tiếp cận.",
            "changing demographics; demographic breakdown; demographic shift", "population statistics, census data", "individual profile"
        ),
        w(
            "urb_municipality", "urban_society", "municipality", "/mjuːˌnɪs.ɪ'pæl.ə.ti/", "noun",
            7.5, "Chính quyền đô thị, chính quyền thành phố", "a city or town that has corporate status and local government",
            "The municipality allocated funds to convert vacant factory sites into community allotments.",
            "Chính quyền đô thị đã phân bổ ngân sách để chuyển đổi các khu nhà máy bỏ hoang thành các mảnh đất trồng trọt cộng đồng.",
            "local municipality; municipal government / services / council", "civic authority, borough, local council", "central government"
        ),
        w(
            "urb_subsidize", "urban_society", "subsidize", "/'sʌb.sɪ.daɪz/", "verb",
            7.0, "Trợ cấp, bao cấp giá", "support financially; pay part of the cost of producing something to reduce its price",
            "The government decided to heavily subsidize monthly commuter rail passes.",
            "Chính phủ đã quyết định trợ cấp mạnh mẽ cho vé tàu tháng của hành khách đi làm.",
            "heavily subsidize; state-subsidized; subsidize housing", "fund, underwrite, sponsor", "tax, penalize"
        ),
        w(
            "urb_pedestrianize", "urban_society", "pedestrianize", "/pəˈdes.tri.ə.naɪz/", "verb",
            8.0, "Biến thành phố đi bộ, cấm xe cộ", "convert a street or area into a pedestrian zone",
            "Plans to pedestrianize the historical commercial boulevard were welcomed by shopkeepers.",
            "Kế hoạch biến đại lộ thương mại lịch sử thành phố đi bộ đã được các chủ cửa hàng đón nhận nồng nhiệt.",
            "pedestrianize a zone / street / center; pedestrianized area", "convert for walkers, car-free zone", "motorize"
        ),
        w(
            "urb_overcrowding", "urban_society", "overcrowding", "/ˌəʊ.və'kraʊ.dɪŋ/", "noun",
            6.5, "Tình trạng quá tải đông đúc", "the presence of more people or things in a space than is desirable or safe",
            "Public hospitals suffer chronic overcrowding during winter respiratory disease waves.",
            "Các bệnh viện công rơi vào tình trạng quá tải kinh niên trong các đợt sóng bệnh đường hô hấp mùa đông.",
            "severe / chronic overcrowding; prevent overcrowding", "congestion, overpopulation, cramming", "spaciousness, emptiness"
        ),
        w(
            "urb_civic", "urban_society", "civic", "/'sɪv.ɪk/", "adj",
            7.0, "Thuộc về công dân và nghĩa vụ công", "relating to a city or town, especially its administration; in relation to citizens' duties",
            "Fostering civic engagement through town hall consultations strengthens democratic cohesion.",
            "Nuôi dưỡng sự tham gia của công dân thông qua các buổi tham vấn tại tòa thị chính giúp tăng cường sự gắn kết dân chủ.",
            "civic duty / responsibility / pride / center / engagement", "municipal, public, community", "private, individual"
        ),
        w(
            "urb_residential", "urban_society", "residential", "/ˌrez.ɪ'den.ʃəl/", "adj",
            6.5, "Thuộc về khu dân cư sinh sống", "designed for people to live in; providing living accommodations",
            "Heavy industrial haulage is strictly prohibited from traversing quiet residential avenues.",
            "Xe vận tải công nghiệp nặng bị nghiêm cấm đi qua các đại lộ dân cư yên tĩnh.",
            "residential area / district / street / quarter", "domestic, suburban, housing", "commercial, industrial"
        ),
        w(
            "urb_revitalization", "urban_society", "revitalization", "/ˌriːˌvaɪ.təl.aɪ'zeɪ.ʃən/", "noun",
            8.0, "Sự tái sinh, hồi sinh khu vực xuống cấp", "the action of imbuing something with new life and vitality; renewal",
            "The waterfront revitalization project introduced open air amphitheaters and green walkways.",
            "Dự án hồi sinh khu bờ sông đã mang lại các nhà hát ngoài trời và những lối đi bộ xanh ngát.",
            "urban revitalization; economic / neighborhood revitalization", "regeneration, renewal, resurgence", "decay, stagnation"
        ),
        w(
            "urb_commute", "urban_society", "commute", "/kə'mjuːt/", "verb",
            6.0, "Di chuyển đi làm hàng ngày", "travel some distance between one's home and place of work on a regular basis",
            "Thousands of suburban residents commute by high-speed electric trains into the city core.",
            "Hàng ngàn cư dân vùng ngoại ô di chuyển đi làm hàng ngày bằng tàu điện cao tốc vào lõi thành phố.",
            "daily commute; commute to work; long / short commute", "travel back and forth, journey to work", "telecommute"
        ),
        w(
            "cul_indigenous", "culture_arts", "indigenous", "/ɪn'dɪdʒ.ɪ.nəs/", "adj",
            7.5, "Bản địa, thổ cư lâu đời", "originating or occurring naturally in a particular place; native",
            "Linguists are working with indigenous elders to document endangered oral storytelling traditions.",
            "Các nhà ngôn ngữ học đang hợp tác với các già làng bản địa để ghi lại truyền thống kể chuyện truyền miệng đang có nguy cơ mai một.",
            "indigenous culture / people / language / wildlife / species", "native, aboriginal, endemic", "introduced, non-native, foreign"
        ),
        w(
            "cul_heritage", "culture_arts", "heritage", "/'her.ɪ.tɪdʒ/", "noun",
            7.0, "Di sản văn hóa truyền đời", "property that is or may be inherited; valued objects and qualities such as historic buildings that have been passed down",
            "The ancient temple complex was designated a UNESCO world heritage sanctuary.",
            "Quần thể đền đài cổ kính đã được công nhận là khu bảo tồn di sản thế giới của UNESCO.",
            "cultural / historical heritage; preserve heritage; heritage site", "legacy, inheritance, tradition", "modern innovation"
        ),
        w(
            "cul_preservation", "culture_arts", "preservation", "/ˌprez.ə'veɪ.ʃən/", "noun",
            7.0, "Sự bảo tồn, giữ gìn nguyên trạng", "the action of preserving something; keeping something in its original state",
            "The archival preservation of delicate papyrus manuscripts requires strict climate control.",
            "Công tác bảo tồn lưu trữ các bản thảo giấy cói mỏng manh đòi hỏi kiểm soát khí hậu nghiêm ngặt.",
            "historic preservation; preservation of records / architecture", "conservation, protection, safeguarding", "destruction, dilapidation"
        ),
        w(
            "cul_pigment", "culture_arts", "pigment", "/'pɪɡ.mənt/", "noun",
            7.5, "Chất màu nhuộm, sắc tố tự nhiên", "the natural coloring matter of animal or plant tissue; a powder mixed with liquid to produce paint",
            "Artisans ground lapis lazuli into ultramarine pigment to paint luminous frescoes.",
            "Các nghệ nhân đã nghiền ngọc lưu ly thành sắc tố màu xanh biển sâu để vẽ những bức tranh tường rực rỡ.",
            "natural / synthetic pigment; paint with pigments; pigment extraction", "coloring, tint, dye", "bleach"
        ),
        w(
            "cul_inscribed", "culture_arts", "inscribed", "/ɪn'skraɪbd/", "adj",
            7.5, "Được khắc chữ, ghi tạc", "written or carved on something; officially registered",
            "The granite monument was inscribed with commemorative odes honoring fallen navigators.",
            "Đài tưởng niệm bằng đá granit được khắc những bài thơ ca tưởng niệm tôn vinh những người đi biển đã khuất.",
            "inscribed on stone; intricately inscribed; inscribed with verses", "carved, engraved, etched", "blank, erased"
        ),
        w(
            "cul_craftsmanship", "culture_arts", "craftsmanship", "/'krɑːfts.mən.ʃɪp/", "noun",
            7.5, "Tay nghề thủ công tinh xảo", "skill in a particular craft; the quality of design and work shown in something made by hand",
            "The delicate joinery of the seventeenth-century bureau testifies to supreme Japanese craftsmanship.",
            "Mộng gỗ ghép tinh xảo của chiếc bàn viết thế kỷ 17 minh chứng cho tay nghề thủ công tuyệt đỉnh của người Nhật.",
            "exquisite / master craftsmanship; quality of craftsmanship", "artistry, workmanship, master skill", "shoddy work, mass production"
        ),
        w(
            "cul_aesthetic", "culture_arts", "aesthetic", "/es'θet.ɪk/", "adj",
            7.5, "Thuộc về mỹ học, có tính thẩm mỹ", "concerned with beauty or the appreciation of beauty; pleasing in appearance",
            "The architect seamlessly reconciled modern functional requirements with classical aesthetic proportions.",
            "Kiến trúc sư đã dung hòa liền mạch các yêu cầu công năng hiện đại với các tỷ lệ thẩm mỹ cổ điển.",
            "aesthetic appeal / value / sensibility; aesthetic standards", "artistic, tasteful, beautiful", "unattractive, displeasing"
        ),
        w(
            "cul_exhibit", "culture_arts", "exhibit", "/ɪɡ'zɪb.ɪt/", "verb",
            6.5, "Trưng bày, biểu lộ triển lãm", "manifest or deliberately display a quality or item publicly",
            "The museum will exhibit rare impressionist canvases never before seen outside private vaults.",
            "Bảo tàng sẽ trưng bày những bức tranh sơn dầu ấn tượng hiếm có chưa từng thấy bên ngoài các kho lưu trữ tư nhân.",
            "exhibit artwork / paintings; exhibit in a gallery", "display, show, unveil", "conceal, hide"
        ),
        w(
            "cul_contemporary", "culture_arts", "contemporary", "/kən'tem.pər.ər.i/", "adj",
            7.0, "Đương đại, thuộc thời nay", "living or occurring at the same time; belonging to or occurring in the present",
            "Contemporary sculpture frequently incorporates recycled plastics to highlight consumerist waste.",
            "Điêu khắc đương đại thường kết hợp nhựa tái chế để nhấn mạnh vấn đề lãng phí của xã hội tiêu dùng.",
            "contemporary art / society / music / literature", "modern, current, present-day", "antiquated, archaic"
        ),
        w(
            "cul_folklore", "culture_arts", "folklore", "/'fəʊk.lɔːr/", "noun",
            7.0, "Văn hóa dân gian, truyện tích dân gian", "the traditional beliefs, customs, and stories of a community, passed through generations by word of mouth",
            "Scandinavian folklore abounds with mystical tales of mountain trolls and sea sirens.",
            "Văn hóa dân gian vùng Scandinavia tràn ngập những câu chuyện thần bí về quỷ lùn trên núi và mỹ nhân ngư.",
            "rich folklore; part of folklore; traditional folklore", "mythology, legends, oral tradition", "documented science"
        ),
        w(
            "cul_tradition", "culture_arts", "tradition", "/trə'dɪʃ.ən/", "noun",
            6.0, "Truyền thống văn hóa phong tục", "the transmission of customs or beliefs from generation to generation",
            "The community honors a centuries-old tradition of lighting clay lanterns during the harvest solstice.",
            "Cộng đồng tôn vinh truyền thống hàng thế kỷ thắp đèn lồng bằng đất sét trong dịp hạ chí thu hoạch mùa màng.",
            "rich / long-standing tradition; follow / break a tradition", "custom, convention, practice", "novelty, innovation"
        ),
        w(
            "cul_curator", "culture_arts", "curator", "/kjʊə'reɪ.tər/", "noun",
            7.5, "Người phụ trách giám tuyển bảo tàng", "a keeper or custodian of a museum or other collection",
            "The chief curator arranged the exhibition chronologically to demonstrate stylistic evolution.",
            "Giám tuyển trưởng đã sắp xếp triển lãm theo trình tự thời gian để thể hiện sự tiến hóa về mặt phong cách.",
            "museum / art curator; head curator", "custodian, keeper, archivist", "visitor"
        ),
        w(
            "idm_eye_to_eye", "idioms", "see eye to eye", "/siː aɪ tuː aɪ/", "idiom",
            7.5, "Đồng thuận, cùng chung quan điểm", "be in full agreement with someone on a matter",
            "My father and I don't always see eye to eye regarding career choices, but we deeply respect each other.",
            "Cha tôi và tôi không phải lúc nào cũng cùng chung quan điểm về lựa chọn nghề nghiệp, nhưng chúng tôi hết sức tôn trọng nhau.",
            "see eye to eye on something; rarely see eye to eye", "agree completely, concur, be on the same page", "disagree, clash"
        ),
        w(
            "idm_arm_and_leg", "idioms", "cost an arm and a leg", "/kɒst ən ɑːm ænd ə leɡ/", "idiom",
            7.0, "Rất đắt đỏ, tốn kém bộn tiền", "be extremely expensive",
            "Renting an apartment in central London costs an arm and a leg for newly graduated students.",
            "Thuê một căn hộ ở trung tâm London tốn kém bộn tiền đối với các sinh viên mới tốt nghiệp.",
            "cost an absolute arm and a leg", "exorbitant, break the bank, prohibitively pricey", "dirt cheap, cost next to nothing"
        ),
        w(
            "idm_eleventh_hour", "idioms", "at the eleventh hour", "/æt ði ɪˈlev.ənθ aʊər/", "idiom",
            7.5, "Vào phút chót, ngay trước khi hết giờ", "at the last possible moment before a deadline or crisis",
            "He submitted his university thesis at the eleventh hour, right before midnight.",
            "Anh ấy đã nộp luận văn đại học vào phút chót, ngay trước nửa đêm.",
            "save at the eleventh hour; change plans at the eleventh hour", "at the last minute, in the nick of time", "well ahead of time"
        ),
        w(
            "idm_midnight_oil", "idioms", "burn the midnight oil", "/bɜːn ðə ˈmɪd.naɪt ɔɪl/", "idiom",
            7.0, "Thức khuya làm việc, cày đêm học bài", "read or work late into the night",
            "I had to burn the midnight oil for three weeks to prepare for my IELTS Academic examination.",
            "Tôi đã phải thức khuya cày đêm suốt ba tuần để chuẩn bị cho kỳ thi IELTS Academic.",
            "burn the midnight oil studying for exams", "pull an all-nighter, work into the small hours", "turn in early"
        ),
        w(
            "idm_on_the_fence", "idioms", "on the fence", "/ɒn ðə fens/", "idiom",
            7.0, "Lưỡng lự, chưa quyết định dứt khoát", "undecided or neutral between two alternative choices",
            "I'm still on the fence about whether to study abroad or seek employment domestically.",
            "Tôi vẫn còn đang lưỡng lự chưa quyết định liệu nên đi du học hay tìm việc trong nước.",
            "sit on the fence; remain on the fence", "undecided, hesitant, vacillating", "decisive, committed"
        ),
        w(
            "idm_bite_bullet", "idioms", "bite the bullet", "/baɪt ðə ˈbʊl.ɪt/", "idiom",
            7.5, "Cắn răng chịu đựng để làm việc khó khăn", "decide to do something difficult or unpleasant that one has been putting off",
            "I finally bit the bullet and enrolled in the intensive pronunciation training course.",
            "Cuối cùng tôi đã cắn răng quyết tâm đăng ký khóa luyện phát âm chuyên sâu.",
            "decide to bite the bullet; bite the bullet and do it", "face the music, grit one's teeth", "procrastinate, shrink away"
        ),
        w(
            "idm_hit_the_books", "idioms", "hit the books", "/hɪt ðə bʊks/", "idiom",
            6.5, "Bắt tay vào học tập chăm chỉ", "begin to study in a determined and concentrated manner",
            "Exam week is arriving, so it's high time to hit the books and revise grammar.",
            "Tuần thi cử đang đến gần rồi, vì vậy đã đến lúc phải bắt tay vào học hành chăm chỉ và ôn lại ngữ pháp.",
            "hit the books hard", "study hard, buckle down", "slay time, slack off"
        ),
        w(
            "idm_call_it_a_day", "idioms", "call it a day", "/kɔːl ɪt ə deɪ/", "idiom",
            6.5, "Dừng làm việc, kết thúc ngày làm", "decide or agree to stop doing what one has been doing for the day",
            "We've reviewed twenty complex flashcards; let's call it a day and recharge.",
            "Chúng ta đã ôn tập 20 thẻ từ vựng phức tạp rồi; hãy dừng làm việc hôm nay để nạp lại năng lượng nhé.",
            "decide to call it a day; ready to call it a day", "pack up, finish up, wrap up", "carry on working"
        ),
        w(
            "idm_play_by_ear", "idioms", "play it by ear", "/pleɪ ɪt baɪ ɪər/", "idiom",
            7.5, "Tùy cơ ứng biến theo tình thế", "proceed spontaneously in a situation without predefined plans",
            "We don't know what questions the examiner will pose in Part 3, so we must play it by ear.",
            "Chúng ta không biết giám khảo sẽ hỏi những câu gì trong Part 3, do vậy chúng ta phải tùy cơ ứng biến.",
            "play things by ear; play it by ear rather than plan", "improvise, ad-lib, wing it", "stick to a rigid plan"
        ),
        w(
            "idm_blessing_in_disguise", "idioms", "a blessing in disguise", "/ə ˈbles.ɪŋ ɪn dɪsˈɡaɪz/", "idiom",
            7.5, "Trong cái rủi có cái may", "an apparent misfortune that eventually has good or beneficial results",
            "Failing the mock exam was a blessing in disguise because it compelled me to overhaul my study strategy.",
            "Trượt kỳ thi thử hóa ra lại là điều trong cái rủi có cái may vì nó thôi thúc tôi đại tu lại chiến lược ôn tập của mình.",
            "turn out to be a blessing in disguise", "hidden benefit, silver lining", "double blow, curse"
        ),
        w(
            "idm_break_the_ice", "idioms", "break the ice", "/breɪk ði aɪs/", "idiom",
            7.0, "Phá tan bầu không khí ngượng ngùng lúc đầu", "do or say something to relieve tension or break awkward silence",
            "The examiner opened with a light question about hometowns to break the ice.",
            "Giám khảo đã mở đầu bằng một câu hỏi nhẹ nhàng về quê hương để phá tan sự ngượng ngùng ban đầu.",
            "icebreaker activity; break the ice with a joke", "ease tension, warm up the room", "freeze up"
        ),
        w(
            "idm_under_the_weather", "idioms", "under the weather", "/ˈʌn.dər ðə ˈweð.ər/", "idiom",
            6.5, "Cảm thấy không được khỏe, khó ở", "slightly unwell or in low spirits",
            "I felt a bit under the weather yesterday, so I postponed my listening practice session.",
            "Hôm qua tôi cảm thấy hơi khó ở trong người nên đã hoãn lại buổi luyện nghe.",
            "feel slightly under the weather", "indisposed, unwell, out of sorts", "in peak health, fit as a fiddle"
        ),
        w(
            "sig_subsequently", "signpost", "subsequently", "/'sʌb.sɪ.kwənt.li/", "adverb",
            7.0, "Sau đó, tiếp theo sau đó", "after a particular event or time has happened",
            "The museum opened in 1995; subsequently, a dedicated educational pavilion was annexed in 2002.",
            "Bảo tàng mở cửa vào năm 1995; sau đó, một gian trưng bày giáo dục chuyên dụng đã được sáp nhập vào năm 2002.",
            "subsequently discovered / revealed / published", "afterward, later on, thereupon", "previously, beforehand"
        ),
        w(
            "sig_furthermore", "signpost", "furthermore", "/ˌfɜː.ðə'mɔːr/", "adverb",
            7.0, "Hơn nữa, vả lại (bổ sung luận điểm)", "in addition; besides (used to introduce a fresh point supporting previous one)",
            "The tuition is reasonable; furthermore, free accommodation is provided for foreign scholars.",
            "Học phí rất hợp lý; hơn nữa, chỗ ở miễn phí còn được cung cấp cho các học giả nước ngoài.",
            "furthermore, evidence indicates that", "moreover, in addition, besides", "nonetheless"
        ),
        w(
            "sig_in_contrast", "signpost", "in contrast", "/ɪn ˈkɒn.trɑːst/", "phrase",
            6.5, "Ngược lại, trái lại (chuyển ý đối lập)", "used to introduce a person or thing that is noticeably different from another",
            "Section A exhibited high rainfall; in contrast, Section B suffered extended droughts.",
            "Khu vực A có lượng mưa cao; ngược lại, Khu vực B lại hứng chịu những đợt hạn hán kéo dài.",
            "in sharp contrast to; stand in contrast", "conversely, on the other hand", "similarly, likewise"
        ),
        w(
            "sig_more_importantly", "signpost", "more importantly", "/mɔːr ɪmˈpɔː.tənt.li/", "phrase",
            7.0, "Quan trọng hơn cả (dấu hiệu đáp án mấu chốt)", "used to emphasize a point that carries greater consequence",
            "The route is scenic, but more importantly, it avoids congested toll plazas.",
            "Tuyến đường này có phong cảnh đẹp, nhưng quan trọng hơn là nó tránh được các trạm thu phí đông đúc.",
            "more importantly, notice that", "crucially, above all, of paramount significance", "incidentally"
        ),
        w(
            "sig_on_the_contrary", "signpost", "on the contrary", "/ɒn ðə ˈkɒn.trə.ri/", "phrase",
            7.5, "Trái lại (phủ định nhận định trước đó)", "used to intensify a denial of what has just been implied or stated",
            "The course was not tedious; on the contrary, every seminar provoked lively discussion.",
            "Khóa học không hề nhàm chán; trái lại, mỗi buổi hội thảo đều khơi dậy những cuộc thảo luận sôi nổi.",
            "quite on the contrary", "quite the opposite, inversely", "equally so"
        ),
        w(
            "sig_as_a_consequence", "signpost", "as a consequence", "/æz ə ˈkɒn.sɪ.kwəns/", "phrase",
            7.0, "Hệ quả là, kết quả là (quan hệ nhân quả)", "as a result of something happening earlier",
            "Funding was slashed; as a consequence, field excavations were abruptly suspended.",
            "Kinh phí bị cắt giảm; hệ quả là, các cuộc khai quật thực địa đã bị đình chỉ đột ngột.",
            "as a direct consequence of", "consequently, as a result, hence", "regardless"
        ),
        w(
            "sig_turning_now_to", "signpost", "turning now to", "/ˈtɜː.nɪŋ naʊ tuː/", "phrase",
            7.5, "Bây giờ chuyển sang phần... (chuyển ý bài giảng)", "used by lecturers to indicate a change in topic during a discourse",
            "Turning now to the statistical distribution of species, let us examine Chart 4.",
            "Bây giờ chuyển sang sự phân bố thống kê của các loài, chúng ta hãy cùng xem xét Biểu đồ 4.",
            "turning now to our next point", "moving on to, with regard to", "reverting back to"
        ),
        w(
            "sig_worth_noting", "signpost", "it is worth noting", "/ɪt ɪz wɜːθ ˈnəʊ.tɪŋ/", "phrase",
            7.5, "Đáng chú ý là (dấu hiệu nhấn mạnh thông tin)", "used to introduce a piece of information that deserves specific attention",
            "It is worth noting that participants were monitored under double-blind conditions.",
            "Điều đáng chú ý là các người tham gia đã được theo dõi trong các điều kiện mù đôi.",
            "it is worth noting that...", "notably, remark that", "negligibly"
        ),
        w(
            "sig_in_other_words", "signpost", "to put it another way", "/tuː pʊt ɪt əˈnʌð.ər weɪ/", "phrase",
            7.0, "Nói cách khác (paraphrase lại ý trước)", "used to explain something in a different, often simpler, manner",
            "To put it another way, species survival hinges entirely on genetic diversity.",
            "Nói một cách khác, sự sống còn của loài phụ thuộc hoàn toàn vào sự đa dạng di truyền.",
            "to put it another way", "in other words, that is to say", "literally"
        ),
        w(
            "sig_in_summary", "signpost", "in summary", "/ɪn ˈsʌm.ər.i/", "phrase",
            6.5, "Tóm lại, tổng kết lại (phần kết Section 4)", "briefly summing up the main points",
            "In summary, renewable transition demands policy enforcement alongside technological innovation.",
            "Tóm lại, việc chuyển đổi sang năng lượng tái tạo đòi hỏi phải thực thi chính sách song song với đổi mới công nghệ.",
            "in summary, we can conclude that", "to sum up, in conclusion, overall", "in detail"
        ),
        w(
            "sig_initially", "signpost", "initially", "/ɪˈnɪʃ.əl.i/", "adverb",
            6.5, "Ban đầu (thường bị sửa lại sau đó trong bài nghe)", "at first; in the beginning",
            "The research team initially budgeted £50,000, but expenses later surged to £80,000.",
            "Nhóm nghiên cứu ban đầu dự trù ngân sách 50.000 bảng, nhưng chi phí sau đó đã tăng vọt lên 80.000 bảng.",
            "initially planned / proposed / thought", "at first, originally, at the outset", "ultimately, finally"
        ),
        w(
            "sig_crucially", "signpost", "crucially", "/ˈkruː.ʃəl.i/", "adverb",
            7.5, "Mang tính quyết định, then chốt", "in a way that has a decisive or crucial importance",
            "Crucially, the volunteer had to sign the confidentiality agreement prior to testing.",
            "Điều mang tính quyết định là tình nguyện viên phải ký thỏa thuận bảo mật trước khi tham gia thử nghiệm.",
            "crucially important; crucially, remember to", "vitally, critically, decisively", "unimportantly, trivially"
        ),
    )

    private val wordById: Map<String, VocabularyWord> = allWords.associateBy { it.id }

    fun getWord(id: String): VocabularyWord? = wordById[id]

    fun getTopic(id: String): VocabularyTopic? = topics.find { it.id == id }

    fun getDeck(id: String): VocabularyDeck? = decks.find { it.id == id }

    fun wordsOfTopic(topicId: String): List<VocabularyWord> = allWords.filter { it.topicId == topicId }

    fun wordsOfDeck(deckId: String): List<VocabularyWord> = when (deckId) {
        DECK_CAM19 -> listOfNotNull(
            getWord("env_deteriorate"), getWord("env_mitigate"), getWord("tech_biometrics"),
            getWord("tech_autonomous"), getWord("sci_archaeological"), getWord("sci_artifact"),
            getWord("hea_sedentary"), getWord("hea_cognitive_bias"), getWord("urb_urbanization"),
            getWord("urb_infrastructure"), getWord("cul_pigment"), getWord("cul_inscribed"),
            getWord("edu_pedagogical"), getWord("tech_breakthrough"), getWord("work_remuneration")
        )
        DECK_CAM18 -> listOfNotNull(
            getWord("env_biodiversity"), getWord("env_ecosystem"), getWord("edu_curriculum"),
            getWord("edu_empirical"), getWord("tech_automation"), getWord("tech_computation"),
            getWord("work_hierarchy"), getWord("work_collaboration"), getWord("sci_excavate"),
            getWord("sci_prehistoric"), getWord("hea_wellbeing"), getWord("hea_epidemic"),
            getWord("urb_congestion"), getWord("urb_revitalization"), getWord("cul_indigenous")
        )
        DECK_CAM17 -> listOfNotNull(
            getWord("env_pollutant"), getWord("env_catastrophic"), getWord("edu_literacy"),
            getWord("edu_comprehend"), getWord("tech_cybersecurity"), getWord("tech_ubiquitous"),
            getWord("work_productivity"), getWord("work_incentive"), getWord("sci_sediment"),
            getWord("sci_specimen"), getWord("hea_therapeutic"), getWord("hea_insomnia"),
            getWord("urb_demographics"), getWord("urb_municipality"), getWord("cul_heritage")
        )
        DECK_CAM16 -> listOfNotNull(
            getWord("env_contamination"), getWord("env_renewable"), getWord("edu_methodology"),
            getWord("edu_proficiency"), getWord("tech_cutting_edge"), getWord("tech_simulate"),
            getWord("work_entrepreneur"), getWord("work_turnover"), getWord("sci_civilization"),
            getWord("sci_durability"), getWord("hea_perception"), getWord("hea_metabolism"),
            getWord("urb_subsidize"), getWord("urb_pedestrianize"), getWord("cul_preservation")
        )
        DECK_WRITING -> allWords.filter { it.topicId in topics.map { t -> t.id } && it.isAdvanced }
        DECK_IDIOMS -> wordsOfTopic(TOPIC_IDIOMS)
        DECK_SIGNPOST -> wordsOfTopic(TOPIC_SIGNPOST)
        else -> emptyList()
    }

    fun deckSubtitle(deckId: String): String {
        val n = wordsOfDeck(deckId).size
        return when (deckId) {
            DECK_CAM19 -> "$n từ trọng tâm Cambridge 19"
            DECK_CAM18 -> "$n từ trọng tâm Cambridge 18"
            DECK_CAM17 -> "$n từ học thuật Cambridge 17"
            DECK_CAM16 -> "$n từ học thuật Cambridge 16"
            DECK_WRITING -> "$n từ academic • Paraphrase"
            DECK_IDIOMS -> "$n cụm từ diễn đạt tự nhiên"
            DECK_SIGNPOST -> "$n từ chuyển hướng & từ khóa"
            else -> "$n từ"
        }
    }

    fun resolveScope(type: String, id: String?): List<VocabularyWord> = when (type) {
        SCOPE_TOPIC -> id?.let { wordsOfTopic(it) } ?: emptyList()
        SCOPE_DECK -> id?.let { wordsOfDeck(it) } ?: emptyList()
        else -> allWords
    }

    fun scopeTitle(type: String, id: String?): String = when (type) {
        SCOPE_TOPIC -> getTopic(id ?: "")?.titleVi ?: "Từ vựng"
        SCOPE_DECK -> when (id) {
            DECK_CAM19 -> "Cambridge 19"
            DECK_CAM18 -> "Cambridge 18"
            DECK_CAM17 -> "Cambridge 17"
            DECK_CAM16 -> "Cambridge 16"
            DECK_WRITING -> "Writing Task 2"
            DECK_IDIOMS -> "Idioms"
            DECK_SIGNPOST -> "Signpost"
            else -> "Từ vựng"
        }
        else -> "Ôn tập tổng hợp"
    }
}
