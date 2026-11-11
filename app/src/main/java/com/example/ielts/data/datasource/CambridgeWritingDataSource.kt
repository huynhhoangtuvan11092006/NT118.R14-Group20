package com.example.ielts.data.datasource

import com.example.ielts.R

/**
 * Model biểu diễn thông tin chi tiết một đề thi IELTS Writing
 */
data class WritingTestItem(
    val id: String,
    val series: String,         // Ví dụ: "Cambridge 18"
    val testNumber: Int,        // 1, 2, ...
    val taskType: String,       // "ACADEMIC_TASK_1" hoặc "TASK_2"
    val title: String,          // Ví dụ: "Cam 18 - Test 1: Task 1"
    val prompt: String,         // Đề bài chi tiết
    val sourceMaterial: String? = null, // Dữ liệu phân tích số liệu biểu đồ (dành cho Task 1 để AI chấm chuẩn xác)
    val imageResId: Int? = null,// Resource hình ảnh biểu đồ trong drawable (nếu có)
    val minWords: Int,          // 150 từ (Task 1) hoặc 250 từ (Task 2)
    val durationMinutes: Int,   // 20 phút (Task 1) hoặc 40 phút (Task 2)
    val topic: String,          // Chủ đề: "Urbanization", "Science & Society", v.v.
    val modelEssay: String? = null // Bài mẫu tham khảo Band 8.0+
)

/**
 * Kho đề thi chuẩn IELTS Writing Cambridge
 * Hướng dẫn thêm đề mới:
 * Chỉ cần gọi list.add(WritingTestItem(...)) vào hàm getAllTests() bên dưới.
 */
object CambridgeWritingDataSource {

    fun getAllTests(): List<WritingTestItem> {
        val list = mutableListOf<WritingTestItem>()

        // ==========================================
        // CAMBRIDGE 18 - TEST 1
        // ==========================================

        // --- Cam 18 Test 1 Task 1 ---
        list.add(
            WritingTestItem(
                id = "cam18-test1-task1",
                series = "Cambridge 18",
                testNumber = 1,
                taskType = "ACADEMIC_TASK_1",
                title = "Cam 18 - Test 1: Task 1 (Urban Population)",
                prompt = "The graph below gives information about the percentage of the population in four Asian countries living in cities from 1970 to 2020, with predictions for 2030 and 2040.\n\nSummarise the information by selecting and reporting the main features, and make comparisons where relevant.",
                sourceMaterial = "Line graph illustrating urban population percentage in 4 Asian countries (Philippines, Malaysia, Thailand, Indonesia) from 1970 to 2040. " +
                        "Malaysia: starts ~30% in 1970, rises steadily to 40% (1980), 45% (1990), 60% (2000), 75% (2020), projected ~83% in 2040 (highest overall). " +
                        "Philippines: starts ~32% in 1970, peaks at ~48% in 1990, dips to ~42% in 2010, rises to ~45% in 2020, projected ~56% in 2040. " +
                        "Indonesia: starts lowest at ~13% in 1970, climbs gradually to 25% (1990), accelerates past Thailand and Philippines after 2000 to reach 52% in 2020, projected 64% in 2040. " +
                        "Thailand: starts ~18% in 1970, remains relatively flat around 30-33% between 1990 and 2020, projected to reach ~50% in 2040.",
                imageResId = R.drawable.img_writing_cam18_test1_task1,
                minWords = 150,
                durationMinutes = 20,
                topic = "Line Graph • Urbanization in Asia",
                modelEssay = """
The line graph illustrates the proportion of urban dwellers across four Asian nations between 1970 and 2020, alongside projected trends extending to 2040.

Overall, it is manifest that all four countries experienced an upward trajectory in urban living over the period, a pattern forecast to persist through 2040. Furthermore, while Malaysia is projected to consolidate its position as the most urbanized population, Indonesia demonstrates the most remarkable expansion.

In 1970, the Philippines had the highest proportion of city residents at approximately 32%, marginally ahead of Malaysia (30%). However, while the Philippines exhibited fluctuating growth—peaking at nearly 48% in 1990 before dipping to around 42% in 2010 and recovering to 45% in 2020—Malaysia underwent rapid, uninterrupted urbanization. By 2020, three-quarters of Malaysians lived in cities, and this figure is projected to exceed 80% by 2040.

Regarding Indonesia and Thailand, only 13% and 18% of their populations were urbanized in 1970, respectively. Indonesia's urban demographic accelerated dramatically after 2000, surpassing Thailand and the Philippines to hit over 50% in 2020, with forecasts indicating a rise to approximately 64% in 2040. Thailand, by contrast, grew more modestly, plateauing near 33% between 1990 and 2020 before an expected surge to 50% by 2040.
                """.trimIndent()
            )
        )

        // --- Cam 18 Test 1 Task 2 ---
        list.add(
            WritingTestItem(
                id = "cam18-test1-task2",
                series = "Cambridge 18",
                testNumber = 1,
                taskType = "TASK_2",
                title = "Cam 18 - Test 1: Task 2 (Aim of Science)",
                prompt = "The most important aim of science should be to improve people's lives.\n\nTo what extent do you agree or disagree with this statement?\n\nGive reasons for your answer and include any relevant examples from your own knowledge or experience.",
                sourceMaterial = null,
                imageResId = null,
                minWords = 250,
                durationMinutes = 40,
                topic = "Agree or Disagree • Science & Society",
                modelEssay = """
Scientific inquiry has fundamentally redefined human civilization over recent centuries. While some argue that science exists solely for the pursuit of theoretical knowledge or industrial progress, I strongly agree with the proposition that the paramount objective of scientific endeavors should always be the amelioration of human well-being.

The primary justification for this view lies in public health and medical advancements. Throughout history, scientific breakthroughs have combated mortal afflictions, doubled life expectancy, and mitigated severe physical suffering. For instance, the rapid synthesis of mRNA vaccines during the COVID-19 pandemic averted catastrophic global fatalities and restored societal functionality. Similarly, research into oncological therapies and genetic engineering directly targets the alleviation of disease. If science were divorced from human survival and comfort, such lifesaving discoveries would lose their ethical compass and social utility.

Furthermore, applying scientific ingenuity to address environmental crises is essential for the sustainable preservation of life on Earth. With escalating climate disruption, agricultural technologies such as drought-resistant crops and laboratory-cultured proteins offer viable solutions to prevent widespread famine. Concurrently, clean energy technologies—encompassing photovoltaics, wind turbines, and solid-state batteries—reduce lethal atmospheric pollution and preserve ecological equilibrium for succeeding generations. Hence, orienting scientific capital toward pragmatic environmental challenges directly elevates human quality of life.

In conclusion, although the pursuit of pure knowledge and curiosity-driven exploration are valuable facets of science, its overarching mandate must remain human-centric. By prioritizing health, humanitarian relief, and ecological sustainability, science fulfills its noble calling as the ultimate instrument of human progress.
                """.trimIndent()
            )
        )

        // ==========================================
        // CAMBRIDGE 18 - TEST 2
        // ==========================================

        // --- Cam 18 Test 2 Task 1 ---
        list.add(
            WritingTestItem(
                id = "cam18-test2-task1",
                series = "Cambridge 18",
                testNumber = 2,
                taskType = "ACADEMIC_TASK_1",
                title = "Cam 18 - Test 2: Task 1 (US Household Income)",
                prompt = "The chart below shows the number of households in the US by their annual income in 2007, 2011 and 2015.\n\nSummarise the information by selecting and reporting the main features, and make comparisons where relevant.",
                sourceMaterial = "Bar chart comparing the number of US households (in millions) categorized across five annual income brackets in 2007, 2011, and 2015. " +
                        "1. Less than $25,000: 25 million in 2007, rising to ~28.5 million in 2011, before falling slightly to ~27.5 million in 2015. " +
                        "2. $25,000–$49,999: ~27 million in 2007, peaking at ~30 million in 2011 (highest for 2011), then declining to ~28.5 million in 2015. " +
                        "3. $50,000–$74,999: exactly constant at 21 million across 2007, 2011, and 2015. " +
                        "4. $75,000–$99,999: lowest group overall, recorded at ~14.5 million in 2007, ~14 million in 2011, and ~15 million in 2015. " +
                        "5. $100,000 or more: ~29.5 million in 2007, dipping to ~27.5 million in 2011, followed by a substantial surge to ~33.5 million in 2015 (the single highest figure on the chart).",
                imageResId = R.drawable.img_writing_cam18_test2_task1,
                minWords = 150,
                durationMinutes = 20,
                topic = "Bar Chart • US Household Income",
                modelEssay = """
The bar chart illustrates the distribution of households in the United States across five annual income categories in three distinct years: 2007, 2011, and 2015.

Overall, the highest income bracket ($100,000 or more) represented the largest cohort in both 2007 and 2015, experiencing the most pronounced net increase. In contrast, middle-income households remained exceptionally stable, while the $75,000 to $99,999 bracket consistently accounted for the smallest proportion of the population.

In 2007, roughly 29.5 million households earned $100,000 or more. Despite dropping to 27.5 million in 2011, this segment expanded substantially to reach approximately 33.5 million in 2015, emerging as the dominant demographic. Conversely, households in the lowest brackets—less than $25,000 and $25,000 to $49,999—displayed inverse trends. Both grew between 2007 and 2011, peaking at nearly 28.5 million and 30 million respectively, before each experiencing a marginal decline in 2015.

Meanwhile, the middle-income group ($50,000–$74,999) maintained an unwavering figure of 21 million households throughout the eight-year period. Finally, households earning between $75,000 and $99,999 consistently registered the lowest numbers, fluctuating narrowly between 14 and 15 million across all surveyed years.
                """.trimIndent()
            )
        )

        // --- Cam 18 Test 2 Task 2 ---
        list.add(
            WritingTestItem(
                id = "cam18-test2-task2",
                series = "Cambridge 18",
                testNumber = 2,
                taskType = "TASK_2",
                title = "Cam 18 - Test 2: Task 2 (University Curriculum)",
                prompt = "Some university students want to learn about other subjects in addition to their main subjects. Others believe it is more important to give all their time and attention to studying for a qualification.\n\nDiscuss both these views and give your own opinion.\n\nGive reasons for your answer and include any relevant examples from your own knowledge or experience.",
                sourceMaterial = null,
                imageResId = null,
                minWords = 250,
                durationMinutes = 40,
                topic = "Discuss Both Views • Higher Education",
                modelEssay = """
There is an ongoing debate regarding the optimal breadth of university education. While an argument can be made that tertiary students should channel their undivided focus exclusively into their major discipline, I believe that embracing supplementary subjects fosters cognitive flexibility and enhances holistic career prospects.

Advocates of strict specialization contend that mastering complex modern fields requires unwavering dedication. Contemporary industries, such as neurosurgery, aeronautical engineering, and corporate law, demand exhaustive technical competence. If undergraduates dilute their cognitive bandwidth across peripheral subjects, they risk acquiring superficial familiarity rather than deep, authoritative expertise. Furthermore, academic qualifications are fiercely competitive; students who dedicate all available resources to their core curriculum are often better positioned to graduate with honors and secure coveted entry-level roles.

Nevertheless, the contemporary labor market increasingly rewards multidisciplinary proficiency and adaptability. Many modern innovations occur precisely at the intersection of disparate fields. For example, a computer science graduate who has studied behavioral psychology is vastly better equipped to design intuitive artificial intelligence interfaces than one who solely writes algorithmic code. Additionally, interdisciplinary learning nurtures critical problem-solving faculties, lateral thinking, and cultural literacy, rendering graduates far more resilient against automation and economic shifts.

In conclusion, although rigorous focus on a chosen specialization is undeniable in building foundational mastery, I maintain that university curricula should actively encourage cross-disciplinary learning. Striking an equilibrium between core domain expertise and broad intellectual exposure prepares students not merely to earn a degree, but to thrive within dynamic 21st-century workplaces.
                """.trimIndent()
            )
        )

        return list
    }

    /**
     * Tìm kiếm một đề thi theo id (hỗ trợ cả dấu gạch ngang '-' lẫn gạch dưới '_')
     */
    fun getTestById(id: String): WritingTestItem? {
        val normalized = id.trim().lowercase().replace('_', '-')
        return getAllTests().firstOrNull { it.id.lowercase().replace('_', '-') == normalized }
    }
}

