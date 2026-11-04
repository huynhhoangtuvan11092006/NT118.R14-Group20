package com.example.ielts.data.datasource

import com.example.ielts.data.remote.dto.ListeningQuestionDetailDto
import com.example.ielts.data.remote.dto.ListeningSectionDto
import com.example.ielts.data.remote.dto.ListeningTestDetailDto

object CambridgeListeningTestDetailDataSource {

    fun getTestDetail(testId: String): ListeningTestDetailDto {
        val allTests = CambridgeListeningDataSource.getAllTests()
        val testItem = allTests.find { it.id == testId } ?: allTests.first()

        val sections = listOf(
            ListeningSectionDto(
                sectionNumber = 1,
                title = testItem.sectionTitles.getOrElse(0) { "Section 1: Customer Order Inquiry" },
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754902/%E5%89%9118_section1-part1.mp3",
                durationSeconds = 420,
                transcript = "[00:05] Agent: Good morning, City Movers and Storage. How can I help you today?\n[00:10] Customer: Hello, I'm calling to inquire about booking a relocation service for next week.\n[00:20] Agent: First, may I take your full name please? Customer: Yes, my first name is Arthur.\n[00:32] Agent: And what is your current address? Customer: It's number 24 Willow Road, Cambridge.\n[00:44] Customer: My mobile number is 07892 345 671."
            ),
            ListeningSectionDto(
                sectionNumber = 2,
                title = testItem.sectionTitles.getOrElse(1) { "Section 2: Tour of Sports Gym & Community" },
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754888/%E5%89%9118_section1-part2.mp3",
                durationSeconds = 450,
                transcript = "[00:02] Guide: Welcome to the tour of our newly revamped community sports gym, nothing but fitness.\n[00:18] The gym's open from 05:30 AM, nice and early in the morning, to 10 PM at night.\n[00:32] We have a childcare centre on-site for moms and dads, but it's only open in the mornings.\n[00:46] There's a healthy cafe on-site open all day."
            ),
            ListeningSectionDto(
                sectionNumber = 3,
                title = testItem.sectionTitles.getOrElse(2) { "Section 3: Students Discussion on Renewable Energy" },
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754901/%E5%89%9118_section1-part3.mp3",
                durationSeconds = 480,
                transcript = "[00:05] Jack: Hi Emma, thanks for meeting up to discuss our environmental engineering assignment on offshore wind energy.\n[00:22] Jack: Coastal winds are considerably stronger, yielding up to 40% more electricity.\n[00:48] Emma: Maritime zoning laws proved to be the primary regulatory obstacle."
            ),
            ListeningSectionDto(
                sectionNumber = 4,
                title = testItem.sectionTitles.getOrElse(3) { "Section 4: University Lecture on Marine Biology" },
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754909/%E5%89%9118_section1-part4.mp3",
                durationSeconds = 495,
                transcript = "[00:05] Professor: Good morning everyone. In today's marine biology lecture, we will explore the evolutionary adaptations of cetaceans.\n[00:19] Cetaceans navigate in dark underwater environments primarily through echolocation.\n[00:32] Sperm whales can dive over 2,000 meters in search of prey."
            )
        )

        val questions = mutableListOf<ListeningQuestionDetailDto>()

        // Part 1: Questions 1 to 10 (Form completion)
        val p1Prompts = listOf(
            "Customer First Name: ________",
            "Delivery Street Address: ________ Willow Road, Cambridge",
            "Contact Phone: ________",
            "Type of Household Goods: ________",
            "Special Protection required for: Kitchen ________",
            "Insurance policy tier: Standard ________ Coverage",
            "Preferred Delivery Date: Next Friday, the ________",
            "Pickup Time Slot: ________ in the morning",
            "Payment Settlement Method: Company ________",
            "Seasonal Flyer Discount Code: ________"
        )
        for (i in 0 until 10) {
            questions.add(
                ListeningQuestionDetailDto(
                    id = "q_${i + 1}",
                    position = i + 1,
                    type = "FILL_IN_BLANK",
                    prompt = p1Prompts[i],
                    sectionNumber = 1,
                    timestampStart = 20 + i * 20,
                    timestampEnd = 35 + i * 20
                )
            )
        }

        // Part 2: Questions 11 to 20 (Multiple Choice)
        val p2Questions = listOf(
            Triple("What time does the fitness gym open on weekdays?", listOf("A. 05:30 AM early morning", "B. 06:00 AM regular hour", "C. 07:00 AM weekend slot"), 2),
            Triple("When are children care services available at the center?", listOf("A. All day round", "B. In the morning only", "C. During evening classes"), 2),
            Triple("What is unique about the on-site nutritional cafe?", listOf("A. Free protein shakes", "B. Serves hot meals all day", "C. Members discount on weekends"), 2),
            Triple("From what time does the sports apparel shop open?", listOf("A. From 09:00 AM", "B. From midday onwards", "C. After 03:00 PM"), 2),
            Triple("Which new equipment has just been installed in the cardio room?", listOf("A. Rowing machines", "B. Latest treadmill models", "C. Stair climbers"), 2),
            Triple("Where is the Finnish sauna located on the floor map?", listOf("A. Next to the main reception", "B. Beside the yoga studio", "C. Inside the changing room"), 2),
            Triple("What feature is provided in the newly renovated change rooms?", listOf("A. Luxury heated showers", "B. Swimming towel rental", "C. Individual lockers with keys"), 2),
            Triple("How can new members book a personal trainer consultation?", listOf("A. Via mobile app", "B. Directly at front desk", "C. By calling the hotline"), 2),
            Triple("What is required before using the Olympic lifting platform?", listOf("A. Physical fitness test", "B. Safety induction seminar", "C. Extra fee membership"), 2),
            Triple("What discount is offered for an annual upfront membership?", listOf("A. 10% cash discount", "B. Two months free access", "C. Complimentary sports bag"), 2)
        )
        for (i in 0 until 10) {
            val q = p2Questions[i]
            questions.add(
                ListeningQuestionDetailDto(
                    id = "q_${i + 11}",
                    position = i + 11,
                    type = "MULTIPLE_CHOICE",
                    prompt = "${i + 11}. ${q.first}",
                    options = q.second,
                    sectionNumber = 2,
                    timestampStart = 15 + i * 25,
                    timestampEnd = 30 + i * 25
                )
            )
        }

        // Part 3: Questions 21 to 30 (Discussion - Multiple Choice)
        val p3Questions = listOf(
            Triple("Why did the students focus on offshore wind farms?", listOf("A. Lower installation costs", "B. Stronger and consistent winds", "C. Less political controversy"), 3),
            Triple("What was the main regulatory obstacle discovered?", listOf("A. Maritime zoning and bird laws", "B. High grid transmission fees", "C. Fishing community protests"), 3),
            Triple("What surprised Jack most about modern turbine generators?", listOf("A. Their physical height", "B. Mechanical efficiency (15 MW)", "C. Ease of offshore repair"), 3),
            Triple("What did Dr. Harrison advise them to include in the report?", listOf("A. Financial feasibility analysis", "B. Full life-cycle carbon assessment", "C. Competitor case studies"), 3),
            Triple("Which task has Emma volunteered to complete?", listOf("A. Quantitative statistical charts", "B. Technical architectural schematics", "C. Final executive summary"), 3),
            Triple("How will Jack gather data on coastal wind velocities?", listOf("A. Meteorological datasets", "B. On-site sensor measurements", "C. Satellite telemetry feeds"), 3),
            Triple("What is their plan for writing the literature review?", listOf("A. Jack writes first draft", "B. Both collaborate together", "C. Emma consults the librarian"), 3),
            Triple("When is their intermediate draft submission deadline?", listOf("A. End of next month", "B. In three weeks time", "C. Next Monday morning"), 3),
            Triple("Which software will they use for fluid dynamics modelling?", listOf("A. OpenFOAM package", "B. MATLAB simulation tools", "C. ANSYS engineering suite"), 3),
            Triple("What was the tutor's feedback on their initial bibliography?", listOf("A. Too outdated", "B. Highly comprehensive and relevant", "C. Needs more peer-reviewed papers"), 3)
        )
        for (i in 0 until 10) {
            val q = p3Questions[i]
            questions.add(
                ListeningQuestionDetailDto(
                    id = "q_${i + 21}",
                    position = i + 21,
                    type = "MULTIPLE_CHOICE",
                    prompt = "${i + 21}. ${q.first}",
                    options = q.second,
                    sectionNumber = 3,
                    timestampStart = 20 + i * 30,
                    timestampEnd = 40 + i * 30
                )
            )
        }

        // Part 4: Questions 31 to 40 (Lecture Notes Completion)
        val p4Prompts = listOf(
            "Primary sensory mechanism in deep water: ________",
            "Maximum recorded dive depth for sperm whales: ________ meters",
            "Chief natural predator threatening juvenile calves: ________ whales",
            "Seasonal migration purpose in summer: Feed in ________ waters",
            "Primary prey species abundance determined by ocean temperature: ________",
            "Long-distance social acoustic communication through melodic ________",
            "Lethal hazard caused by plastic ingestion: Internal digestive ________",
            "Navigational disorientation exacerbated by commercial ________ noise",
            "Urgent international policy needed: Global ________ agreements",
            "Target conservation goal: Preserve cetaceans for ________ generations"
        )
        for (i in 0 until 10) {
            questions.add(
                ListeningQuestionDetailDto(
                    id = "q_${i + 31}",
                    position = i + 31,
                    type = "FILL_IN_BLANK",
                    prompt = p4Prompts[i],
                    sectionNumber = 4,
                    timestampStart = 25 + i * 35,
                    timestampEnd = 50 + i * 35
                )
            )
        }

        return ListeningTestDetailDto(
            id = testItem.id,
            title = testItem.title,
            summary = testItem.summary,
            audioUrl = testItem.audioUrl,
            durationMinutes = testItem.durationMinutes,
            sections = sections,
            questions = questions
        )
    }
}

