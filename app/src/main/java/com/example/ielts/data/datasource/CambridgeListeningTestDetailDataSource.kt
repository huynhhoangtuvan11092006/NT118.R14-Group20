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
                durationSeconds = 420,
                transcript = "[00:00] MAN: Excuse me. Would you mind if I asked you some questions? We're doing a survey on transport.\n[00:08] SADIE: Yes, that's OK.\n[00:11] MAN: First of all, can I take your name?\n[00:14] SADIE: Yes. It's Sadie Jones.\n[00:18] MAN: Thanks very much. And could I have your date of birth – just the year will do, actually. Is that all right?\n[00:26] SADIE: Yes, that's fine. It's 1991.\n[00:31] MAN: So next your postcode, please.\n[00:34] SADIE: It's DW3Q 7YZ. [Q1]\n[00:39] MAN: Great. Thanks. Is that in Wells?\n[00:43] SADIE: No, it's actually in Harborne – Wells isn't far from there, though.\n[00:50] MAN: I really like that area. My grandmother lived there when I was a kid.\n[00:56] SADIE: Yes, it is nice.\n[01:00] MAN: Right, so now I want to ask you some questions about how you travelled here today. Did you use public transport?\n[01:09] SADIE: Yes. I came by bus.\n[01:13] MAN: OK. And that was today. It's the 24th of April, isn't it? [Q2]\n[01:20] SADIE: Isn't it the 25th? No, actually, you're right.\n[01:26] MAN: Ha ha. And what was the reason for your trip today? I can see you've got some shopping with you.\n[01:34] SADIE: Yes. I did some shopping but the main reason I came here was to go to the dentist. [Q3]\n[01:43] MAN: That's not much fun. Hope it was nothing serious.\n[01:48] SADIE: No, it was just a check-up. It's fine.\n[01:52] MAN: Good. Do you normally travel by bus into the city centre?\n[01:58] SADIE: Yes. I stopped driving in ages ago because parking was so difficult to find and it costs so much. [Q4]\n[02:08] MAN: I see.\n[02:10] SADIE: The bus is much more convenient too. It only takes about 30 minutes.\n[02:17] MAN: That's good. So where did you start your journey?\n[02:22] SADIE: At the bus stop on Claxby Street. [Q5]\n[02:27] MAN: Is that C-L-A-X-B-Y?\n[02:30] SADIE: That's right.\n[02:35] MAN: And how satisfied with the service are you? Do you have any complaints?\n[02:42] SADIE: Well, as I said, it's very convenient and quick when it's on time, but this morning it was late. Only about 10 minutes, but still. [Q6]\n[02:55] MAN: Yes, I understand that's annoying. And what about the timetable? Do you have any comments about that?\n[03:03] SADIE: Mmm. I suppose I mainly use the bus during the day, but any time I've been in town in the evening – for dinner or at the cinema – I've noticed you have to wait a long time for a bus – there aren't that many. [Q7]\n[03:23] MAN: OK, thanks. So now I'd like to ask you about your car use.\n[03:29] SADIE: Well, I have got a car but I don't use it that often. Mainly just to go to the supermarket. But that's about it really. My husband uses it at the weekends to go to the golf club. [Q8]\n[03:48] MAN: And what about a bicycle?\n[03:51] SADIE: I don't actually have one at the moment.\n[03:55] MAN: What about the city bikes you can rent? Do you ever use those?\n[04:01] SADIE: No – I'm not keen on cycling there because of all the pollution. But I would like to get a bike – it would be good to use it to get to work. [Q9]\n[04:17] MAN: So why haven't you got one now?\n[04:20] SADIE: Well, I live in a flat – on the second floor and it doesn't have any storage – so we'd have to leave it in the hall outside the flat. [Q10]\n[04:34] MAN: I see. OK. Well, I think that's all …"
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
            ListeningQuestionDetailDto(
                id = "q_1",
                position = 1,
                type = "FILL_IN_BLANK",
                prompt = "Postcode: ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 34,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_2",
                position = 2,
                type = "FILL_IN_BLANK",
                prompt = "Travelling by bus\nDate of bus journey: ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 73,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_3",
                position = 3,
                type = "FILL_IN_BLANK",
                prompt = "Reason for trip: shopping and visit to the ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 94,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_4",
                position = 4,
                type = "FILL_IN_BLANK",
                prompt = "Travelled by bus because cost of ________ too high",
                options = null,
                sectionNumber = 1,
                timestampStart = 118,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_5",
                position = 5,
                type = "FILL_IN_BLANK",
                prompt = "Got on bus at ________ Street",
                options = null,
                sectionNumber = 1,
                timestampStart = 142,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_6",
                position = 6,
                type = "FILL_IN_BLANK",
                prompt = "Complaints about bus service: - bus today was ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 162,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_7",
                position = 7,
                type = "FILL_IN_BLANK",
                prompt = "Complaints about bus service: - frequency of buses in the ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 183,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_8",
                position = 8,
                type = "FILL_IN_BLANK",
                prompt = "Travelling by car: Goes to the ________ by car",
                options = null,
                sectionNumber = 1,
                timestampStart = 209,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_9",
                position = 9,
                type = "FILL_IN_BLANK",
                prompt = "Dislikes travelling by bike in the city centre because of the ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 241,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_10",
                position = 10,
                type = "FILL_IN_BLANK",
                prompt = "Doesn't own a bike because of a lack of ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 260,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_11",
                position = 11,
                type = "MULTIPLE_CHOICE",
                prompt = "11. Why does the speaker apologise about the seats?",
                options = listOf("A. They are too small.", "B. There are not enough of them.", "C. Some of them are very close together."),
                sectionNumber = 2,
                timestampStart = 10,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_12",
                position = 12,
                type = "MULTIPLE_CHOICE",
                prompt = "12. What does the speaker say about the age of volunteers?",
                options = listOf("A. The age of volunteers is less important than other factors.", "B. Young volunteers are less reliable than older ones.", "C. Most volunteers are about 60 years old."),
                sectionNumber = 2,
                timestampStart = 25,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_13",
                position = 13,
                type = "MULTIPLE_CHOICE",
                prompt = "13. What does the speaker say about training?",
                options = listOf("A. It is continuous.", "B. It is conducted by a manager.", "C. It takes place online."),
                sectionNumber = 2,
                timestampStart = 42,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_14",
                position = 14,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 14 and 15: Choose TWO letters, A–E]\nWhich TWO issues does the speaker ask the audience to consider before they apply to be volunteers?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. their financial situation", "B. their level of commitment", "C. their work experience", "D. their ambition", "E. their availability"),
                sectionNumber = 2,
                timestampStart = 60,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_15",
                position = 15,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 14 and 15: Choose TWO letters, A–E]\nWhich TWO issues does the speaker ask the audience to consider before they apply to be volunteers?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. their financial situation", "B. their level of commitment", "C. their work experience", "D. their ambition", "E. their availability"),
                sectionNumber = 2,
                timestampStart = 60,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_16",
                position = 16,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 16–20: What does the speaker suggest would be helpful for each area of voluntary work? Choose FIVE answers from the box, A–G]\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: Fundraising",
                options = listOf("A. experience on stage", "B. original, new ideas", "C. parenting skills", "D. an understanding of food and diet", "E. retail experience", "F. a good memory", "G. a good level of fitness"),
                sectionNumber = 2,
                timestampStart = 85,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_17",
                position = 17,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 16–20: What does the speaker suggest would be helpful for each area of voluntary work? Choose FIVE answers from the box, A–G]\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: Litter collection",
                options = listOf("A. experience on stage", "B. original, new ideas", "C. parenting skills", "D. an understanding of food and diet", "E. retail experience", "F. a good memory", "G. a good level of fitness"),
                sectionNumber = 2,
                timestampStart = 105,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_18",
                position = 18,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 16–20: What does the speaker suggest would be helpful for each area of voluntary work? Choose FIVE answers from the box, A–G]\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 'Playmates'",
                options = listOf("A. experience on stage", "B. original, new ideas", "C. parenting skills", "D. an understanding of food and diet", "E. retail experience", "F. a good memory", "G. a good level of fitness"),
                sectionNumber = 2,
                timestampStart = 125,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_19",
                position = 19,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 16–20: What does the speaker suggest would be helpful for each area of voluntary work? Choose FIVE answers from the box, A–G]\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: Story club",
                options = listOf("A. experience on stage", "B. original, new ideas", "C. parenting skills", "D. an understanding of food and diet", "E. retail experience", "F. a good memory", "G. a good level of fitness"),
                sectionNumber = 2,
                timestampStart = 145,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_20",
                position = 20,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 16–20: What does the speaker suggest would be helpful for each area of voluntary work? Choose FIVE answers from the box, A–G]\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: First aid",
                options = listOf("A. experience on stage", "B. original, new ideas", "C. parenting skills", "D. an understanding of food and diet", "E. retail experience", "F. a good memory", "G. a good level of fitness"),
                sectionNumber = 2,
                timestampStart = 165,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_21",
                position = 21,
                type = "MULTIPLE_CHOICE",
                prompt = "21. What problem did Chantal have at the start of the talk?",
                options = listOf("A. Her view of the speaker was blocked.", "B. She was unable to find an empty seat.", "C. The students next to her were talking."),
                sectionNumber = 3,
                timestampStart = 12,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_22",
                position = 22,
                type = "MULTIPLE_CHOICE",
                prompt = "22. What were Hugo and Chantal surprised to hear about the job market?",
                options = listOf("A. It has become more competitive than it used to be.", "B. There is more variety in it than they had realised.", "C. Some areas of it are more exciting than others."),
                sectionNumber = 3,
                timestampStart = 24,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_23",
                position = 23,
                type = "MULTIPLE_CHOICE",
                prompt = "23. Hugo and Chantal agree that the speaker’s message was",
                options = listOf("A. unfair to them at times.", "B. hard for them to follow.", "C. critical of the industry."),
                sectionNumber = 3,
                timestampStart = 40,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_24",
                position = 24,
                type = "MULTIPLE_CHOICE",
                prompt = "24. What do Hugo and Chantal criticise about their school careers advice?",
                options = listOf("A. when they received the advice", "B. how much advice was given", "C. who gave the advice"),
                sectionNumber = 3,
                timestampStart = 55,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_25",
                position = 25,
                type = "MULTIPLE_CHOICE",
                prompt = "25. When discussing their future, Hugo and Chantal disagree on",
                options = listOf("A. which is the best career in fashion.", "B. when to choose a career in fashion.", "C. why they would like a career in fashion."),
                sectionNumber = 3,
                timestampStart = 70,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_26",
                position = 26,
                type = "MULTIPLE_CHOICE",
                prompt = "26. How does Hugo feel about being an unpaid assistant?",
                options = listOf("A. He is realistic about the practice.", "B. He feels the practice is dishonest.", "C. He thinks others want to change the practice."),
                sectionNumber = 3,
                timestampStart = 88,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_27",
                position = 27,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 27 and 28: Choose TWO letters, A–E]\nWhich TWO mistakes did the speaker admit she made in her first job?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. being dishonest to her employer", "B. paying too much attention to how she looked", "C. expecting to become well known", "D. trying to earn a lot of money", "E. openly disliking her client"),
                sectionNumber = 3,
                timestampStart = 105,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_28",
                position = 28,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 27 and 28: Choose TWO letters, A–E]\nWhich TWO mistakes did the speaker admit she made in her first job?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. being dishonest to her employer", "B. paying too much attention to how she looked", "C. expecting to become well known", "D. trying to earn a lot of money", "E. openly disliking her client"),
                sectionNumber = 3,
                timestampStart = 105,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_29",
                position = 29,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 29 and 30: Choose TWO letters, A–E]\nWhich TWO pieces of retail information do Hugo and Chantal agree would be useful?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. the reasons people return fashion items", "B. how much time people have to shop for clothes", "C. fashion designs people want but can’t find", "D. the best time of year for fashion buying", "E. the most popular fashion sizes"),
                sectionNumber = 3,
                timestampStart = 130,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_30",
                position = 30,
                type = "MULTIPLE_CHOICE",
                prompt = "[Questions 29 and 30: Choose TWO letters, A–E]\nWhich TWO pieces of retail information do Hugo and Chantal agree would be useful?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. the reasons people return fashion items", "B. how much time people have to shop for clothes", "C. fashion designs people want but can’t find", "D. the best time of year for fashion buying", "E. the most popular fashion sizes"),
                sectionNumber = 3,
                timestampStart = 130,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_31",
                position = 31,
                type = "FILL_IN_BLANK",
                prompt = "Problems caused by elephant overpopulation:\n• damage to 31 ________ in the park",
                options = null,
                sectionNumber = 4,
                timestampStart = 18,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_32",
                position = 32,
                type = "FILL_IN_BLANK",
                prompt = "The translocation process:\n• a suitable group of elephants from the same 32 ________ was selected",
                options = null,
                sectionNumber = 4,
                timestampStart = 32,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_33",
                position = 33,
                type = "FILL_IN_BLANK",
                prompt = "The translocation process:\n• vets and park staff made use of 33 ________ to help guide the elephants into an open plain",
                options = null,
                sectionNumber = 4,
                timestampStart = 48,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_34",
                position = 34,
                type = "FILL_IN_BLANK",
                prompt = "The translocation process:\n• elephants were immobilised with tranquilisers\n- this process had to be completed quickly to reduce 34 ________",
                options = null,
                sectionNumber = 4,
                timestampStart = 65,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_35",
                position = 35,
                type = "FILL_IN_BLANK",
                prompt = "The translocation process:\n- elephants had to be turned on their 35 ________ to avoid damage to their lungs",
                options = null,
                sectionNumber = 4,
                timestampStart = 80,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_36",
                position = 36,
                type = "FILL_IN_BLANK",
                prompt = "The translocation process:\n- elephants' 36 ________ had to be monitored constantly",
                options = null,
                sectionNumber = 4,
                timestampStart = 98,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_37",
                position = 37,
                type = "FILL_IN_BLANK",
                prompt = "The translocation process:\n- data including the size of their tusks and 37 ________ was taken",
                options = null,
                sectionNumber = 4,
                timestampStart = 112,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_38",
                position = 38,
                type = "FILL_IN_BLANK",
                prompt = "Advantages of translocation at Nkhotakota Wildlife Park:\n• 38 ________ opportunities",
                options = null,
                sectionNumber = 4,
                timestampStart = 130,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_39",
                position = 39,
                type = "FILL_IN_BLANK",
                prompt = "Advantages of translocation at Nkhotakota Wildlife Park:\n• a reduction in the number of poachers and 39 ________",
                options = null,
                sectionNumber = 4,
                timestampStart = 146,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_40",
                position = 40,
                type = "FILL_IN_BLANK",
                prompt = "Advantages of translocation at Nkhotakota Wildlife Park:\n• an increase in 40 ________ as a contributor to GDP",
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
