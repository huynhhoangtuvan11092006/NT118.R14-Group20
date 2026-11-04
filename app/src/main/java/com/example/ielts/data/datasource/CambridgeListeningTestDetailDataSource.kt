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
                transcript = "[00:05] Agent: Good morning, City Movers and Storage. How can I help you today?\n[00:10] Customer: Hello, I'm calling to inquire about booking a relocation service for next week.\n[00:16] Agent: Certainly! First, may I take your full name please?\n[00:20] Customer: Yes, my first name is Arthur, spelled A-R-T-H-U-R.\n[00:27] Agent: Thank you, Mr. Arthur. And what is your current address for pickup?\n[00:32] Customer: It's number 24 Willow Road, Cambridge.\n[00:38] Agent: Got it, 24 Willow Road. And could you provide a reliable contact telephone number?\n[00:44] Customer: Of course, my mobile is 07892 345 671.\n[00:52] Agent: Great. Now, what type of goods are we moving primarily?\n[00:56] Customer: Mostly household furniture, such as beds, wardrobes, and dining tables.\n[01:05] Agent: Understood. Do you require any specialized packing boxes for fragile items?\n[01:10] Customer: Yes, especially for our kitchen glassware and fine porcelain dishes.\n[01:18] Agent: We provide full protection cartons for kitchenware. Would you also like to add transit insurance coverage?\n[01:25] Customer: Yes, please include standard insurance against any accidental damage.\n[01:32] Agent: Perfect. What date would you like the moving team to arrive?\n[01:36] Customer: We are aiming for next Friday, the 18th.\n[01:42] Agent: Friday the 18th is available. Do you prefer a morning or afternoon slot?\n[01:47] Customer: The morning would be best, around 8:30 AM before traffic builds up.\n[01:54] Agent: Excellent. Regarding payment method, will that be by credit card or bank cheque?\n[02:00] Customer: A company cheque from my employer will be issued.\n[02:08] Agent: Noted. Lastly, do you have any promotion voucher code?\n[02:13] Customer: Yes, I have a seasonal discount code from your online flyer: RELOC10.\n[02:20] Agent: Fantastic! That applies a 10% saving on your total invoice."
            ),
            ListeningSectionDto(
                sectionNumber = 2,
                title = testItem.sectionTitles.getOrElse(1) { "Section 2: Tour of Sports Gym & Community" },
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754888/%E5%89%9118_section1-part2.mp3",
                durationSeconds = 450,
                transcript = "[00:02] Guide: Welcome to the tour of our newly revamped community sports gym, nothing but fitness.\n[00:08] No doubt you know a little of what our gym has to give in terms of fitness, but as you'll discover, we have much more than just running machines and weights on offer.\n[00:18] The gym's open from 05:30 AM, nice and early in the morning, to 10 PM at night, so these hours give you plenty of time to fit in your workouts.\n[00:32] Now onto children's services: we have a childcare centre on-site for moms and dads wanting to fit a workout into your busy day, but it's only open in the mornings.\n[00:46] For those of you too busy to eat properly at home, there's a cafe on-site offering healthy and delicious meals that starts serving as soon as the gym opens.\n[01:01] We have a sports shop here for those of you who'd like to pick up some new shoes or workout gear. The staff there will be happy to kit you out from midday onwards.\n[01:17] We're a modern gym, and we're always updating our facilities. Actually, the gym's just replaced all our treadmills with the latest models.\n[01:29] Furthermore, we have thoroughly renovated and modernized the change rooms with luxury heated showers and digital lockers.\n[01:39] Now, let me orient you using the floor map: directly across from reception is our yoga studio at point A, and right beside it at point B is our brand new Finnish sauna.\n[01:54] Personal training consultations can be booked easily directly through our mobile application.\n[02:10] Before using the Olympic lifting platform, a brief 15-minute safety induction seminar is required.\n[02:25] For members signing up for an upfront annual pass, we offer two months free access."
            ),
            ListeningSectionDto(
                sectionNumber = 3,
                title = testItem.sectionTitles.getOrElse(2) { "Section 3: Students Discussion on Renewable Energy" },
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754901/%E5%89%9118_section1-part3.mp3",
                durationSeconds = 480,
                transcript = "[00:05] Jack: Hi Emma, thanks for meeting up to discuss our environmental engineering assignment on offshore wind energy.\n[00:12] Emma: Hi Jack! I've been reviewing our research questions. Why did we decide to focus specifically on offshore wind farms rather than onshore turbines?\n[00:22] Jack: Well, the main reason was that coastal winds are considerably stronger and much more consistent throughout the year, yielding up to 40% more electricity.\n[00:34] Emma: That's right. However, when examining policy constraints, the primary regulatory obstacle proved to be maritime zoning laws and environmental protection agreements for migratory sea birds.\n[00:48] Jack: Exactly. What truly surprised me in the technical literature was the sheer mechanical efficiency of the latest 15-megawatt turbine generators.\n[01:00] Emma: Our tutor, Dr. Harrison, suggested that we must include a life-cycle carbon assessment covering raw material extraction and offshore foundation installation.\n[01:14] Jack: Agreed. For our project submission, who is doing what? Emma, would you be able to produce the technical diagrams and architectural schematics?\n[01:24] Emma: Sure, I can draw the diagrams. And I can also conduct the field interviews with local harbor engineers.\n[01:32] Jack: Great! I will handle the quantitative statistical analysis on wind velocity datasets.\n[01:40] Emma: And for the literature review and final concluding section, let's both collaborate on writing those together."
            ),
            ListeningSectionDto(
                sectionNumber = 4,
                title = testItem.sectionTitles.getOrElse(3) { "Section 4: University Lecture on Marine Biology" },
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754909/%E5%89%9118_section1-part4.mp3",
                durationSeconds = 495,
                transcript = "[00:05] Professor: Good morning everyone. In today's marine biology lecture, we will explore the evolutionary adaptations and ecological challenges faced by cetaceans, including whales and dolphins.\n[00:19] Cetaceans navigate and hunt in dark underwater environments primarily through echolocation, emitting high-frequency clicks and interpreting the returning sound echoes.\n[00:32] Deep-diving species like sperm whales can descend to extraordinary ocean depths exceeding 2,000 meters in search of giant squid.\n[00:44] In these abyssal zones, avoiding apex predators such as killer whales becomes critical for juvenile calves.\n[00:54] Annual seasonal migration across thousands of kilometers allows baleen whales to feed in cold polar waters during summer and breed in warm equatorial lagoons.\n[01:10] Ocean temperature variations directly influence the abundance of krill, their primary prey species.\n[01:21] Complex social communication relies on intricate melodic whale songs that can propagate hundreds of miles across ocean basins.\n[01:34] Unfortunately, anthropogenic plastic pollution presents a lethal hazard through ingestion and digestive blockages.\n[01:46] Moreover, commercial shipping noise interferes with their delicate sensory navigation, leading to catastrophic disorientation and strandings.\n[02:00] Effective global conservation agreements are urgent to preserve these majestic marine mammals for future generations."
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

