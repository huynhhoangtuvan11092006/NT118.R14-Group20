package com.example.ielts.data.datasource

import com.example.ielts.data.remote.dto.ListeningQuestionDetailDto
import com.example.ielts.data.remote.dto.ListeningSectionDto
import com.example.ielts.data.remote.dto.ListeningTestDetailDto

object CambridgeListeningTestDetailDataSource {

    fun getTestDetail(testId: String, testTitle: String? = null): ListeningTestDetailDto {
        val allTests = CambridgeListeningDataSource.getAllTests()
        val testItem = allTests.find { it.id == testId }
            ?: allTests.find { testTitle != null && it.title.equals(testTitle, ignoreCase = true) }
            ?: if (testId.contains("18") || testTitle?.contains("18") == true) {
                allTests.find { it.id == "cam18-listening-test1" } ?: allTests.first()
            } else {
                allTests.first()
            }

        val sections = listOf(
            ListeningSectionDto(
                sectionNumber = 1,
                title = "Part 1: Transport survey",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754902/%E5%89%9118_section1-part1.mp3",
                durationSeconds = 430,
                transcript = "[01:30] MAN: Excuse me. Would you mind if I asked you some questions? We're doing a survey on transport.\n[01:38] SADIE: Yes, that's OK.\n[01:40] MAN: First of all, can I take your name?\n[01:43] SADIE: Yes. It's Sadie Jones.\n[01:46] MAN: Thanks very much. And could I have your date of birth – just the year will do, actually. Is that all right?\n[01:53] SADIE: Yes, that's fine. It's 1991.\n[01:58] MAN: So next your postcode, please.\n[02:01] SADIE: It's DW3Q 7YZ. [Q1]\n[02:08] MAN: Great. Thanks. Is that in Wells?\n[02:10] SADIE: No, it's actually in Harborne. Wells isn't far from there, though.\n[02:16] MAN: I really like that area. My grandmother lived there when I was a kid.\n[02:21] SADIE: Yes, it is nice.\n[02:23] MAN: Right, so now I want to ask you some questions about how you travelled here today. Did you use public transport?\n[02:32] SADIE: Yes. I came by bus.\n[02:34] MAN: OK. And that was today. It's the 24th of April, isn't it? [Q2]\n[02:40] SADIE: Isn't it the 25th? No, actually, you're right.\n[02:47] MAN: Ha ha. And what was the reason for your trip today? I can see you've got some shopping with you.\n[02:53] SADIE: Yes. I did some shopping but the main reason I came here was to go to the dentist. [Q3]\n[03:00] MAN: That's not much fun. Hope it was nothing serious.\n[03:04] SADIE: No, it was just a check-up. It's fine.\n[03:07] MAN: Good. Do you normally travel by bus into the city centre?\n[03:12] SADIE: Yes. I stopped driving in ages ago because parking was so difficult to find and it costs so much. [Q4]\n[03:21] MAN: I see.\n[03:22] SADIE: The bus is much more convenient too. It only takes about 30 minutes.\n[03:27] MAN: That's good. So where did you start your journey?\n[03:31] SADIE: At the bus stop on Claxby Street. [Q5]\n[03:35] MAN: Is that C-L-A-X-B-Y?\n[03:40] SADIE: That's right.\n[03:44] MAN: Before you hear the rest of the conversation, you have some time to look at questions 6 to 10.\n[04:22] MAN: And how satisfied with the service are you? Do you have any complaints?\n[04:26] SADIE: Well, as I said, it's very convenient and quick when it's on time.\n[04:33] SADIE: But this morning it was late. [Q6]\n[04:38] SADIE: Only about 10 minutes, but still.\n[04:38] MAN: Yes, I understand. That's annoying.\n[04:41] MAN: And what about the timetable? Do you have any comments about that?\n[04:44] SADIE: Hmm. I suppose I mainly use the bus during the day.\n[04:51] SADIE: But any time I've been in town in the evening, for dinner or at the cinema,\n[04:57] SADIE: I've noticed you have to wait a long time for a bus. There aren't that many. [Q7]\n[05:03] MAN: OK, thanks. So now I'd like to ask you about your car use.\n[05:08] SADIE: Well, I have got a car, but I don't use it that often. Mainly just to go to the supermarket. [Q8]\n[05:17] SADIE: But that's about it really. My husband uses it at the weekends to go to the golf club.\n[05:23] MAN: And what about a bicycle?\n[05:25] SADIE: I don't actually have one at the moment.\n[05:28] MAN: What about the city bikes you can rent? Do you ever use those?\n[05:32] SADIE: No, I'm not keen on cycling there because of all the pollution. [Q9]\n[05:37] SADIE: But I would like to get a bike. It would be good to use it to get to work.\n[05:43] MAN: So why haven't you got one now?\n[05:45] SADIE: Well, I live in a flat on the second floor and it doesn't have any storage, [Q10]\n[05:52] SADIE: so we'd have to leave it in the hall outside the flat.\n[05:56] MAN: I see. OK. Well, I think that's all we need from you today, Sadie.\n[06:04] MAN: That is the end of Part 1. You now have one minute to check your answers to Part 1."
            ),
            ListeningSectionDto(
                sectionNumber = 2,
                title = "Part 2: Becoming a volunteer for ACE",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754888/%E5%89%9118_section1-part2.mp3",
                durationSeconds = 450,
                transcript = "[00:02] Speaker: Good evening everyone, and welcome to this introductory session on volunteering for ACE.\n[00:10] First, my apologies about the seating arrangements tonight. Some of the seats are placed very close together, which isn't ideal.\n[00:25] Now, people often ask about our age requirements. The age of volunteers is really less important than other factors like reliability and enthusiasm.\n[00:42] Regarding training: we ensure it is continuous throughout your journey with us.\n[01:00] Before applying, please consider two key issues: your level of commitment and your availability during weekends.\n[01:25] Let me outline the various roles: For fundraising, original and new ideas are particularly welcome.\n[01:45] For litter collection, volunteers need a good level of fitness.\n[02:05] For 'Playmates', our nutrition program, an understanding of food and diet is essential.\n[02:25] In our Story club, having experience on stage helps captivate young audiences.\n[02:45] And for first aid duties, having a good memory for emergency procedures is vital."
            ),
            ListeningSectionDto(
                sectionNumber = 3,
                title = "Part 3: Talk on jobs in fashion design",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754901/%E5%89%9118_section1-part3.mp3",
                durationSeconds = 480,
                transcript = "[00:05] Hugo: Hi Chantal, how did you find the guest lecture on fashion industry careers?\n[00:12] Chantal: Well, at the beginning I struggled because the students sitting next to me were talking loudly.\n[00:24] Hugo: Same here! But what surprised both of us was that there is much more variety in the job market than we had realised.\n[00:40] Chantal: Absolutely. Though we both agreed the speaker's main message was quite hard for us to follow at points.\n[00:55] Hugo: And our school careers advice was delivered far too late in the academic year.\n[01:10] Chantal: When looking to our own futures, you and I differ on why we would like a career in fashion.\n[01:28] Hugo: Regarding unpaid internships: I feel realistic about the practice – it's just the norm.\n[01:45] Chantal: The speaker admitted two early mistakes: paying too much attention to how she looked, and expecting to become well known right away.\n[02:10] Hugo: We also found two valuable retail statistics: the reasons why people return items, and the fashion designs customers want but can't find."
            ),
            ListeningSectionDto(
                sectionNumber = 4,
                title = "Part 4: Elephant translocation",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754909/%E5%89%9118_section1-part4.mp3",
                durationSeconds = 495,
                transcript = "[00:05] Lecturer: Today we examine the large-scale elephant translocation from Majete National Park.\n[00:18] Overpopulation caused intense competition and extensive damage to fences in the reserve.\n[00:32] In the translocation, an entire family unit was selected to keep social structures intact.\n[00:48] Vets and park rangers employed helicopters to herd the animals towards open plains.\n[01:05] Tranquilising had to be swift to minimise physiological stress.\n[01:20] Elephants were carefully positioned on their sides to prevent their immense weight from crushing their lungs.\n[01:38] Their breathing was monitored constantly throughout transit.\n[01:52] Scientists recorded biological data including tusk lengths and the circumference of their feet.\n[02:10] At Nkhotakota Reserve, the project created valuable employment opportunities for local trackers.\n[02:26] Heavy patrols caused a sharp decline in poachers and confiscated weapons.\n[02:42] Ultimately, eco-tourism flourished, providing a significant boost to the national GDP."
            ),
        )

        val questions = listOf(
            // ==================== PART 1: Transport survey ====================
            ListeningQuestionDetailDto(
                id = "q_1",
                position = 1,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nName: Sadie Jones\nYear of birth: 1991\nPostcode: 1 ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 121,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_2",
                position = 2,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nTravelling by bus\nDate of bus journey: 2 ________\nReason for trip: shopping and visit to the [ 3 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 154,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_3",
                position = 3,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nTravelling by bus\n• Date of bus journey: [ 2 ]\n• Reason for trip: shopping and visit to the 3 ________\n• Travelled by bus because cost of [ 4 ] too high",
                options = null,
                sectionNumber = 1,
                timestampStart = 173,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_4",
                position = 4,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nTravelling by bus\n• Reason for trip: shopping and visit to the [ 3 ]\n• Travelled by bus because cost of 4 ________ too high\n• Got on bus at [ 5 ] Street",
                options = null,
                sectionNumber = 1,
                timestampStart = 192,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_5",
                position = 5,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nTravelling by bus\n• Travelled by bus because cost of [ 4 ] too high\n• Got on bus at 5 ________ Street\n• Complaints about bus service:\n  - bus today was [ 6 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 211,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_6",
                position = 6,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nTravelling by bus\n• Got on bus at [ 5 ] Street\n• Complaints about bus service:\n  - bus today was 6 ________\n  - frequency of buses in the [ 7 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 273,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_7",
                position = 7,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nTravelling by bus\n• Complaints about bus service:\n  - bus today was [ 6 ]\n  - frequency of buses in the 7 ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 297,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_8",
                position = 8,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nTravelling by car\n• Goes to the 8 ________ by car",
                options = null,
                sectionNumber = 1,
                timestampStart = 308,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_9",
                position = 9,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nTravelling by bicycle\n• Dislikes travelling by bike in the city centre because of the 9 ________\n• Doesn't own a bike because of a lack of [ 10 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 332,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_10",
                position = 10,
                type = "FILL_IN_BLANK",
                prompt = "Transport survey\nTravelling by bicycle\n• Dislikes travelling by bike in the city centre because of the [ 9 ]\n• Doesn't own a bike because of a lack of 10 ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 345,
                timestampEnd = 0
            ),

            // ==================== PART 2: Becoming a volunteer for ACE ====================
            ListeningQuestionDetailDto(
                id = "q_11",
                position = 11,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 11–13: Choose the correct letter, A, B or C.\n\n11. Why does the speaker apologise about the seats?",
                options = listOf("A. They are too small.", "B. There are not enough of them.", "C. Some of them are very close together."),
                sectionNumber = 2,
                timestampStart = 10,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_12",
                position = 12,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 11–13: Choose the correct letter, A, B or C.\n\n12. What does the speaker say about the age of volunteers?",
                options = listOf("A. The age of volunteers is less important than other factors.", "B. Young volunteers are less reliable than older ones.", "C. Most volunteers are about 60 years old."),
                sectionNumber = 2,
                timestampStart = 25,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_13",
                position = 13,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 11–13: Choose the correct letter, A, B or C.\n\n13. What does the speaker say about training?",
                options = listOf("A. It is continuous.", "B. It is conducted by a manager.", "C. It takes place online."),
                sectionNumber = 2,
                timestampStart = 42,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_14",
                position = 14,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 14 and 15: Choose TWO letters, A–E.\nWhich TWO issues does the speaker ask the audience to consider before they apply to be volunteers?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. their financial situation", "B. their level of commitment", "C. their work experience", "D. their ambition", "E. their availability"),
                sectionNumber = 2,
                timestampStart = 60,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_15",
                position = 15,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 14 and 15: Choose TWO letters, A–E.\nWhich TWO issues does the speaker ask the audience to consider before they apply to be volunteers?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. their financial situation", "B. their level of commitment", "C. their work experience", "D. their ambition", "E. their availability"),
                sectionNumber = 2,
                timestampStart = 60,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_16",
                position = 16,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 16. Fundraising",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 85,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_17",
                position = 17,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 17. Litter collection",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 105,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_18",
                position = 18,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 18. 'Playmates'",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 125,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_19",
                position = 19,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 19. Story club",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 145,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_20",
                position = 20,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 20. First aid",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 165,
                timestampEnd = 0
            ),

            // ==================== PART 3: Talk on jobs in fashion design ====================
            ListeningQuestionDetailDto(
                id = "q_21",
                position = 21,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n21. What problem did Chantal have at the start of the talk?",
                options = listOf("A. Her view of the speaker was blocked.", "B. She was unable to find an empty seat.", "C. The students next to her were talking."),
                sectionNumber = 3,
                timestampStart = 12,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_22",
                position = 22,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n22. What were Hugo and Chantal surprised to hear about the job market?",
                options = listOf("A. It has become more competitive than it used to be.", "B. There is more variety in it than they had realised.", "C. Some areas of it are more exciting than others."),
                sectionNumber = 3,
                timestampStart = 24,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_23",
                position = 23,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n23. Hugo and Chantal agree that the speaker's message was",
                options = listOf("A. unfair to them at times.", "B. hard for them to follow.", "C. critical of the industry."),
                sectionNumber = 3,
                timestampStart = 40,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_24",
                position = 24,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n24. What do Hugo and Chantal criticise about their school careers advice?",
                options = listOf("A. when they received the advice", "B. how much advice was given", "C. who gave the advice"),
                sectionNumber = 3,
                timestampStart = 55,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_25",
                position = 25,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n25. When discussing their future, Hugo and Chantal disagree on",
                options = listOf("A. which is the best career in fashion.", "B. when to choose a career in fashion.", "C. why they would like a career in fashion."),
                sectionNumber = 3,
                timestampStart = 70,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_26",
                position = 26,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n26. How does Hugo feel about being an unpaid assistant?",
                options = listOf("A. He is realistic about the practice.", "B. He feels the practice is dishonest.", "C. He thinks others want to change the practice."),
                sectionNumber = 3,
                timestampStart = 88,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_27",
                position = 27,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 27 and 28: Choose TWO letters, A–E.\nWhich TWO mistakes did the speaker admit she made in her first job?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. being dishonest to her employer", "B. paying too much attention to how she looked", "C. expecting to become well known", "D. trying to earn a lot of money", "E. openly disliking her client"),
                sectionNumber = 3,
                timestampStart = 105,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_28",
                position = 28,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 27 and 28: Choose TWO letters, A–E.\nWhich TWO mistakes did the speaker admit she made in her first job?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. being dishonest to her employer", "B. paying too much attention to how she looked", "C. expecting to become well known", "D. trying to earn a lot of money", "E. openly disliking her client"),
                sectionNumber = 3,
                timestampStart = 105,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_29",
                position = 29,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 29 and 30: Choose TWO letters, A–E.\nWhich TWO pieces of retail information do Hugo and Chantal agree would be useful?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. the reasons people return fashion items", "B. how much time people have to shop for clothes", "C. fashion designs people want but can't find", "D. the best time of year for fashion buying", "E. the most popular fashion sizes"),
                sectionNumber = 3,
                timestampStart = 130,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_30",
                position = 30,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 29 and 30: Choose TWO letters, A–E.\nWhich TWO pieces of retail information do Hugo and Chantal agree would be useful?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. the reasons people return fashion items", "B. how much time people have to shop for clothes", "C. fashion designs people want but can't find", "D. the best time of year for fashion buying", "E. the most popular fashion sizes"),
                sectionNumber = 3,
                timestampStart = 130,
                timestampEnd = 0
            ),

            // ==================== PART 4: Elephant translocation ====================
            ListeningQuestionDetailDto(
                id = "q_31",
                position = 31,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nReasons for overpopulation at Majete National Park\n• strict enforcement of anti-poaching laws\n• successful breeding\n\nProblems caused by elephant overpopulation\n• greater competition, causing hunger for elephants\n• damage to 31 ________ in the park",
                options = null,
                sectionNumber = 4,
                timestampStart = 18,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_32",
                position = 32,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• a suitable group of elephants from the same 32 ________ was selected\n• vets and park staff made use of [ 33 ] to help guide the elephants into an open plain",
                options = null,
                sectionNumber = 4,
                timestampStart = 32,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_33",
                position = 33,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• a suitable group of elephants from the same [ 32 ] was selected\n• vets and park staff made use of 33 ________ to help guide the elephants into an open plain\n• elephants were immobilised with tranquilisers",
                options = null,
                sectionNumber = 4,
                timestampStart = 48,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_34",
                position = 34,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• vets and park staff made use of [ 33 ] to help guide the elephants into an open plain\n• elephants were immobilised with tranquilisers\n  - this process had to be completed quickly to reduce 34 ________\n  - elephants had to be turned on their [ 35 ] to avoid damage to their lungs",
                options = null,
                sectionNumber = 4,
                timestampStart = 65,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_35",
                position = 35,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• elephants were immobilised with tranquilisers\n  - this process had to be completed quickly to reduce [ 34 ]\n  - elephants had to be turned on their 35 ________ to avoid damage to their lungs\n  - elephants' [ 36 ] had to be monitored constantly",
                options = null,
                sectionNumber = 4,
                timestampStart = 80,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_36",
                position = 36,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• elephants were immobilised with tranquilisers\n  - elephants had to be turned on their [ 35 ] to avoid damage to their lungs\n  - elephants' 36 ________ had to be monitored constantly\n  - tracking devices were fitted to the matriarchs",
                options = null,
                sectionNumber = 4,
                timestampStart = 98,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_37",
                position = 37,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• elephants' [ 36 ] had to be monitored constantly\n• tracking devices were fitted to the matriarchs\n• data including the size of their tusks and 37 ________ was taken\n• elephants were taken by truck to their new reserve",
                options = null,
                sectionNumber = 4,
                timestampStart = 112,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_38",
                position = 38,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nAdvantages of translocation at Nkhotakota Wildlife Park\n• 38 ________ opportunities\n• a reduction in the number of poachers and [ 39 ]\n• an example of conservation that other parks can follow",
                options = null,
                sectionNumber = 4,
                timestampStart = 130,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_39",
                position = 39,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nAdvantages of translocation at Nkhotakota Wildlife Park\n• [ 38 ] opportunities\n• a reduction in the number of poachers and 39 ________\n• an example of conservation that other parks can follow",
                options = null,
                sectionNumber = 4,
                timestampStart = 146,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_40",
                position = 40,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nAdvantages of translocation at Nkhotakota Wildlife Park\n• a reduction in the number of poachers and [ 39 ]\n• an example of conservation that other parks can follow\n• an increase in 40 ________ as a contributor to GDP",
                options = null,
                sectionNumber = 4,
                timestampStart = 162,
                timestampEnd = 0
            ),
        )
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
