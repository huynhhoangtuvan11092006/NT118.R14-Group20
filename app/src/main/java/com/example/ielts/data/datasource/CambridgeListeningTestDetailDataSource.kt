package com.example.ielts.data.datasource

import com.example.ielts.data.remote.dto.ListeningQuestionDetailDto
import com.example.ielts.data.remote.dto.ListeningSectionDto
import com.example.ielts.R
import com.example.ielts.data.remote.dto.ListeningTestDetailDto

object CambridgeListeningTestDetailDataSource {

    fun getTestDetail(testId: String, testTitle: String? = null): ListeningTestDetailDto {
        val allTests = CambridgeListeningDataSource.getAllTests()
        val testItem = allTests.find { it.id == testId }
            ?: allTests.find { testTitle != null && it.title.equals(testTitle, ignoreCase = true) }
            ?: if (testId.contains("test4") || testTitle?.contains("Test 4") == true) {
                allTests.find { it.id == "cam18-listening-test4" } ?: allTests.first()
            } else if (testId.contains("test3") || testTitle?.contains("Test 3") == true) {
                allTests.find { it.id == "cam18-listening-test3" } ?: allTests.first()
            } else if (testId.contains("test2") || testTitle?.contains("Test 2") == true) {
                allTests.find { it.id == "cam18-listening-test2" } ?: allTests.first()
            } else if (testId.contains("18") || testTitle?.contains("18") == true) {
                allTests.find { it.id == "cam18-listening-test1" } ?: allTests.first()
            } else {
                allTests.first()
            }

        return if (testItem.id == "cam18-listening-test4" || testTitle?.contains("Test 4") == true || testId.contains("test4")) {
            getCam18Test4Detail(testItem)
        } else if (testItem.id == "cam18-listening-test3" || testTitle?.contains("Test 3") == true || testId.contains("test3")) {
            getCam18Test3Detail(testItem)
        } else if (testItem.id == "cam18-listening-test2" || testTitle?.contains("Test 2") == true || testId.contains("test2")) {
            getCam18Test2Detail(testItem)
        } else {
            getCam18Test1Detail(testItem)
        }
    }

    private fun getCam18Test1Detail(testItem: com.example.ielts.data.remote.dto.ListeningTestListItemDto): ListeningTestDetailDto {
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
                durationSeconds = 400,
                transcript = "[00:57] WOMAN: Good evening everyone. Let me start by welcoming you all to this talk and thanking you for taking the time to consider joining ACE Voluntary Organisation.\n[01:08] WOMAN: ACE offers support to people and services in the local area and we're now looking for more volunteers to help us do this.\n[01:18] WOMAN: By the way, I hope you're all comfortable. We have brought in extra seats so that no one has to stand but it does mean that the people at the back of the room may be a bit squashed. [Q11]\n[01:30] WOMAN: We'll only be here for about half an hour so hopefully that's OK.\n[01:35] WOMAN: One of the first questions we're often asked is how old you need to be to volunteer.\n[01:41] WOMAN: Well, you can be as young as 16 or you can be 60 or over. It all depends on what type of voluntary work you want to do.\n[01:51] WOMAN: Other considerations, such as reliability, are crucial in voluntary work and age isn't related to these in our experience. [Q12]\n[02:01] WOMAN: Another question we get asked relates to training. Well, there's plenty of that and it's all face-to-face.\n[02:10] WOMAN: What's more, training doesn't end when you start working for us. It takes place before, during and after periods of work. [Q13]\n[02:20] WOMAN: Often it's run by other experienced volunteers as managers tend to prefer to get on with other things.\n[02:27] WOMAN: Now, I would ask you to consider a couple of important issues before you decide to apply for voluntary work.\n[02:35] WOMAN: We don't worry about why you want to be a volunteer. People have many different reasons that range from getting work experience to just doing something they've always wanted to do.\n[02:48] WOMAN: But it is critical that you have enough hours in the day for whatever role we agree is suitable for you. [Q15]\n[02:55] WOMAN: If being a volunteer becomes stressful, then it's best not to do it at all.\n[03:01] WOMAN: You may think that your income is important, but we don't ask about that.\n[03:11] WOMAN: It's up to you to decide if you can work without earning money.\n[03:16] WOMAN: What we value is dedication. [Q14]\n[03:24] WOMAN: Some of our most loyal volunteers earn very little themselves but still give their full energy to the work they do with us.\n[03:24] WOMAN: Before you hear the rest of the talk, you have some time to look at questions 16 to 20.\n[03:58] WOMAN: OK, so let's take a look at some of the work areas that we need volunteers for and the sort of things that would help you in those.\n[04:02] WOMAN: Now listen and answer questions 16 to 20.\n[04:11] WOMAN: You may wish simply to help us raise money. If you have the creativity to come up with an imaginative or novel way of fundraising, we'd be delighted, as standing in the local streets or shops with a collection box can be rather boring. [Q16]\n[04:29] WOMAN: One outdoor activity that we need volunteers for is litter collection, and for this it's useful if you can walk for long periods, sometimes uphill. [Q17]\n[04:41] WOMAN: Some of our regular collectors are quite elderly, but very active and keen to protect the environment.\n[04:49] WOMAN: If you enjoy working with children, we have three vacancies for what are called playmates.\n[04:57] WOMAN: These volunteers help children learn about staying healthy through a range of out-of-school activities.\n[05:03] WOMAN: You don't need to have children yourself, but it's good if you know something about nutrition and can give clear instructions. [Q18]\n[05:12] WOMAN: If that doesn't appeal to you, maybe you would be interested in helping out at our Story Club for disabled children, especially if you have done some acting. [Q19]\n[05:24] WOMAN: We put on three performances a year based on books they have read, and we're always looking for support with the theatrical side of this.\n[05:33] WOMAN: The last area I'll mention today is first aid.\n[05:38] WOMAN: Volunteers who join this group can end up teaching others in vulnerable groups who may be at risk of injury.\n[05:45] WOMAN: Initially, though, your priority will be to take in a lot of information and not forget any important steps or details. [Q20]\n[05:54] WOMAN: Right, so does anyone have any questions for me about...\n[06:02] WOMAN: That is the end of Part 2. You now have 30 seconds to check your answers to Part 2."
            ),
            ListeningSectionDto(
                sectionNumber = 3,
                title = "Part 3: Talk on jobs in fashion design",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754901/%E5%89%9118_section1-part3.mp3",
                durationSeconds = 420,
                transcript = "[00:55] HUGO: Hi Chantal. What did you think of the talk, then?\n[01:13] CHANTAL: Hi Hugo. I thought it was good once I'd moved seats.\n[01:17] HUGO: Oh- were the people beside you chatting or something?\n[01:20] CHANTAL: It wasn't that. I went early so that I'd get a seat and not have to stand, but then this guy sat right in front of me and he was so tall!\n[01:30] HUGO: It's hard to see through people's heads, isn't it? [Q21]\n[01:34] CHANTAL: Impossible! Anyway, to answer your question, I thought it was really interesting, especially what the speaker said about the job market.\n[01:44] HUGO: Me too. I mean we know we're going into a really competitive field so it's obvious that we may struggle to get work.\n[01:52] CHANTAL: That's right – and we know we can't all have that 'dream job'.\n[01:59] HUGO: Yeah, but it looks like there's a whole range of … areas of work that we hadn't even thought of – like fashion journalism, for instance. [Q22]\n[02:09] CHANTAL: Yeah – I wasn't expecting so many career options.\n[02:14] HUGO: Mmm. Overall, she had quite a strong message, didn't she?\n[02:23] CHANTAL: She did. She kept saying things like 'I know you all think this, but …' and then she'd tell us how it really is.\n[02:29] HUGO: Perhaps she thinks students are a bit narrow-minded about the industry.\n[02:35] CHANTAL: It was a bit harsh, though! We know it's a tough industry.\n[02:40] HUGO: Yeah – and we're only first years, after all. We've got a lot to learn. [Q23]\n[02:47] CHANTAL: Exactly. Do you think our secondary-school education should have been more career-focused?\n[02:54] HUGO: Well, we had numerous talks on careers, which was good, but none of them were very inspiring. They could have asked more people like today's speaker to talk to us. [Q24]\n[03:07] CHANTAL: I agree. We were told about lots of different careers – just when we needed to be, but not by the experts who really know stuff.\n[03:17] HUGO: So did today's talk influence your thoughts on what career you'd like to take up in the future?\n[03:24] CHANTAL: Well. I promised myself that I'd go through this course and keep an open mind till the end. [Q25]\n[03:33] HUGO: But I think it's better to pick an area of the industry now and then aim to get better and better at it.\n[03:42] CHANTAL: Well, I think we'll just have to differ on that issue!\n[03:47] HUGO: One thing's for certain, though. From what she said, we'll be unpaid assistants in the industry for quite a long time.\n[03:56] CHANTAL: Mmm.\n[03:58] HUGO: I'm prepared for that, aren't you? [Q26]\n[04:03] CHANTAL: Actually, I'm not going to accept that view.\n[04:08] HUGO: Really? But she knows it's the case- and everyone else says the same.\n[04:14] CHANTAL: That doesn't mean it has to be true for me.\n[04:18] HUGO: OK. Well – I hope you're right!\n[04:23] CHANTAL: I thought the speaker's account of her first job was fascinating.\n[04:28] HUGO: Yeah – she admitted she was lucky to get work being a personal dresser for a musician. She didn't even apply for the job and there she was getting paid to choose all his clothes.\n[04:40] CHANTAL: It must have felt amazing – though she said all she was looking for back then was experience, not financial reward.\n[04:49] HUGO: Mmm. And then he was so mean, telling her she was more interested in her own appearance than his! [Q27-28]\n[04:58] CHANTAL: But – she did realise he was right about that, which really made me think. I'm always considering my own clothes but now I can see you should be focusing on your client!\n[05:10] HUGO: She obviously regretted losing the job.\n[05:14] CHANTAL: Well, as she said, she should have hidden her negative feelings about him, but she didn't. [Q27-28]\n[05:23] HUGO: It was really brave the way she picked herself up and took that job in retail. Fancy working in a shop after that!\n[05:31] CHANTAL: Yeah – well, she recommended we all do it at some point. I guess as a designer you'd get to find out some useful information, like how big or small the average shopper is.\n[05:45] HUGO: I think that's an issue for manufacturers, not designers. However, it would be useful to know if there's a gap in the market – you know, an item that no one's stocking but that consumers are looking for. [Q29-30]\n[05:59] CHANTAL: Yeah, people don't give up searching. They also take things back to the store if they aren't right.\n[06:06] HUGO: Yeah. Imagine you worked in an expensive shop and you found out the garments sold there were being returned because they … fell apart in the wash! [Q29-30]\n[06:17] CHANTAL: Yeah, it would be good to know that kind of thing.\n[06:21] HUGO: Yeah."
            ),
            ListeningSectionDto(
                sectionNumber = 4,
                title = "Part 4: Elephant translocation",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754909/%E5%89%9118_section1-part4.mp3",
                durationSeconds = 430,
                transcript = "[01:14] WOMAN: For my presentation today I want to tell you about how groups of elephants have been moved and settled in new reserves. This is known as translocation and has been carried out in Malawi in Africa in recent years. The reason this is being done is because of overpopulation of elephants in some areas.\n[01:39] WOMAN: Overpopulation is a good problem to have and not one we tend to hear about very often. In Malawi's Majete National Park the elephant population had been wiped out by poachers, who killed the elephants for their ivory. But in 2003, the park was restocked and effective law enforcement was introduced. Since then, not a single elephant has been poached. In this safe environment, the elephant population boomed.\n[02:17] WOMAN: Breeding went so well that there were more elephants than the park could support.\n[02:23] WOMAN: This led to a number of problems. Firstly, there was more competition for food, which meant that some elephants were suffering from hunger. As there was a limit to the amount of food in the national park, some elephants began looking further afield. Elephants were routinely knocking down fences around the park, which then had to be repaired at a significant cost. [Q31]\n[02:50] WOMAN: To solve this problem, the decision was made to move dozens of elephants from Majete National Park to Nkhotakota Wildlife Park, where there were no elephants. But, obviously, attempting to move significant numbers of elephants to a new home 300 kilometres away is quite a challenge.\n[03:10] WOMAN: So how did this translocation process work in practice?\n[03:15] WOMAN: Elephants were moved in groups of between eight and twenty, all belonging to one family. [Q32] Because relationships are very important to elephants, they all had to be moved at the same time.\n[03:30] WOMAN: A team of vets and park rangers flew over the park in helicopters and targeted a group, which were rounded up and directed to a designated open plain. [Q33]\n[03:43] WOMAN: The vets then used darts to immobilise the elephants – this was a tricky manoeuvre, as they not only had to select the right dose of tranquiliser for different-sized elephants but they had to dart the elephants as they were running around.\n[04:02] WOMAN: This also had to be done as quickly as possible so as to minimise the stress caused. As soon as the elephants began to flop onto the ground, the team moved in to take care of them. [Q34]\n[04:16] WOMAN: To avoid the risk of suffocation, the team had to make sure none of the elephants were lying on their chests because their lungs could be crushed in this position. So all the elephants had to be placed on their sides. [Q35] One person stayed with each elephant while they waited for the vets to do checks.\n[04:37] WOMAN: It was very important to keep an eye on their breathing – if there were fewer than six breaths per minute, the elephant would need urgent medical attention. [Q36]\n[04:49] WOMAN: Collars were fitted to the matriarch in each group so their movements could be tracked in their new home. Measurements were taken of each elephant's tusks – elephants with large tusks would be at greater risk from poachers – and also of their feet. The elephants were then taken to a recovery area before being loaded onto trucks and transported to their new home. [Q37]\n[05:16] WOMAN: The elephants translocated to Nkhotakota settled in very well and the project has generally been accepted to have been a huge success – and not just for the elephants. Employment prospects have improved enormously, contributing to rising living standards for the whole community. [Q38]\n[05:36] WOMAN: Poaching is no longer an issue, as former poachers are able to find more reliable sources of income. In fact, many of them volunteered to give up their weapons, as they were no longer of any use to them. [Q39]\n[05:55] WOMAN: More than two dozen elephants have been born at Nkhotakota since relocation. With an area of more than 1,800 square kilometres, there's plenty of space for the elephant population to continue to grow. Their presence is also helping to rebalance Nkhotakota's damaged ecosystem and providing a sustainable conservation model, which could be replicated in other parks.\n[06:21] WOMAN: All this has been a big draw for tourism, which contributes five times more than the illegal wildlife trade to GDP, and this is mainly because of the elephants. There's also been a dramatic rise in interest … [Q40]"
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
                timestampStart = 78,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_12",
                position = 12,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 11–13: Choose the correct letter, A, B or C.\n\n12. What does the speaker say about the age of volunteers?",
                options = listOf("A. The age of volunteers is less important than other factors.", "B. Young volunteers are less reliable than older ones.", "C. Most volunteers are about 60 years old."),
                sectionNumber = 2,
                timestampStart = 111,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_13",
                position = 13,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 11–13: Choose the correct letter, A, B or C.\n\n13. What does the speaker say about training?",
                options = listOf("A. It is continuous.", "B. It is conducted by a manager.", "C. It takes place online."),
                sectionNumber = 2,
                timestampStart = 130,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_14",
                position = 14,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 14 and 15: Choose TWO letters, A–E.\nWhich TWO issues does the speaker ask the audience to consider before they apply to be volunteers?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. their financial situation", "B. their level of commitment", "C. their work experience", "D. their ambition", "E. their availability"),
                sectionNumber = 2,
                timestampStart = 196,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_15",
                position = 15,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 14 and 15: Choose TWO letters, A–E.\nWhich TWO issues does the speaker ask the audience to consider before they apply to be volunteers?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. their financial situation", "B. their level of commitment", "C. their work experience", "D. their ambition", "E. their availability"),
                sectionNumber = 2,
                timestampStart = 168,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_16",
                position = 16,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 16. Fundraising",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 251,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_17",
                position = 17,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 17. Litter collection",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 269,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_18",
                position = 18,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 18. 'Playmates'",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 303,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_19",
                position = 19,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 19. Story club",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 312,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_20",
                position = 20,
                type = "MULTIPLE_CHOICE",
                prompt = "Becoming a volunteer for ACE\nQuestions 16–20\nWhat does the speaker suggest would be helpful for each of the following areas of voluntary work?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 16–20.\n\nHelpful things volunteers might offer:\nA. experience on stage\nB. original, new ideas\nC. parenting skills\nD. an understanding of food and diet\nE. retail experience\nF. a good memory\nG. a good level of fitness\n\nArea of voluntary work: 20. First aid",
                options = listOf("A", "B", "C", "D", "E", "F", "G"),
                sectionNumber = 2,
                timestampStart = 345,
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
                timestampStart = 90,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_22",
                position = 22,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n22. What were Hugo and Chantal surprised to hear about the job market?",
                options = listOf("A. It has become more competitive than it used to be.", "B. There is more variety in it than they had realised.", "C. Some areas of it are more exciting than others."),
                sectionNumber = 3,
                timestampStart = 119,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_23",
                position = 23,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n23. Hugo and Chantal agree that the speaker's message was",
                options = listOf("A. unfair to them at times.", "B. hard for them to follow.", "C. critical of the industry."),
                sectionNumber = 3,
                timestampStart = 160,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_24",
                position = 24,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n24. What do Hugo and Chantal criticise about their school careers advice?",
                options = listOf("A. when they received the advice", "B. how much advice was given", "C. who gave the advice"),
                sectionNumber = 3,
                timestampStart = 174,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_25",
                position = 25,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n25. When discussing their future, Hugo and Chantal disagree on",
                options = listOf("A. which is the best career in fashion.", "B. when to choose a career in fashion.", "C. why they would like a career in fashion."),
                sectionNumber = 3,
                timestampStart = 204,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_26",
                position = 26,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 21–26: Choose the correct letter, A, B or C.\n\n26. How does Hugo feel about being an unpaid assistant?",
                options = listOf("A. He is realistic about the practice.", "B. He feels the practice is dishonest.", "C. He thinks others want to change the practice."),
                sectionNumber = 3,
                timestampStart = 238,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_27",
                position = 27,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 27 and 28: Choose TWO letters, A–E.\nWhich TWO mistakes did the speaker admit she made in her first job?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. being dishonest to her employer", "B. paying too much attention to how she looked", "C. expecting to become well known", "D. trying to earn a lot of money", "E. openly disliking her client"),
                sectionNumber = 3,
                timestampStart = 289,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_28",
                position = 28,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 27 and 28: Choose TWO letters, A–E.\nWhich TWO mistakes did the speaker admit she made in her first job?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. being dishonest to her employer", "B. paying too much attention to how she looked", "C. expecting to become well known", "D. trying to earn a lot of money", "E. openly disliking her client"),
                sectionNumber = 3,
                timestampStart = 314,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_29",
                position = 29,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 29 and 30: Choose TWO letters, A–E.\nWhich TWO pieces of retail information do Hugo and Chantal agree would be useful?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. the reasons people return fashion items", "B. how much time people have to shop for clothes", "C. fashion designs people want but can't find", "D. the best time of year for fashion buying", "E. the most popular fashion sizes"),
                sectionNumber = 3,
                timestampStart = 345,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_30",
                position = 30,
                type = "MULTIPLE_CHOICE",
                prompt = "Talk on jobs in fashion design\nQuestions 29 and 30: Choose TWO letters, A–E.\nWhich TWO pieces of retail information do Hugo and Chantal agree would be useful?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. the reasons people return fashion items", "B. how much time people have to shop for clothes", "C. fashion designs people want but can't find", "D. the best time of year for fashion buying", "E. the most popular fashion sizes"),
                sectionNumber = 3,
                timestampStart = 366,
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
                timestampStart = 143,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_32",
                position = 32,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• a suitable group of elephants from the same 32 ________ was selected\n• vets and park staff made use of [ 33 ] to help guide the elephants into an open plain",
                options = null,
                sectionNumber = 4,
                timestampStart = 195,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_33",
                position = 33,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• a suitable group of elephants from the same [ 32 ] was selected\n• vets and park staff made use of 33 ________ to help guide the elephants into an open plain\n• elephants were immobilised with tranquilisers",
                options = null,
                sectionNumber = 4,
                timestampStart = 210,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_34",
                position = 34,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• vets and park staff made use of [ 33 ] to help guide the elephants into an open plain\n• elephants were immobilised with tranquilisers\n  - this process had to be completed quickly to reduce 34 ________\n  - elephants had to be turned on their [ 35 ] to avoid damage to their lungs",
                options = null,
                sectionNumber = 4,
                timestampStart = 242,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_35",
                position = 35,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• elephants were immobilised with tranquilisers\n  - this process had to be completed quickly to reduce [ 34 ]\n  - elephants had to be turned on their 35 ________ to avoid damage to their lungs\n  - elephants' [ 36 ] had to be monitored constantly",
                options = null,
                sectionNumber = 4,
                timestampStart = 256,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_36",
                position = 36,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• elephants were immobilised with tranquilisers\n  - elephants had to be turned on their [ 35 ] to avoid damage to their lungs\n  - elephants' 36 ________ had to be monitored constantly\n  - tracking devices were fitted to the matriarchs",
                options = null,
                sectionNumber = 4,
                timestampStart = 277,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_37",
                position = 37,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nThe translocation process\n• elephants' [ 36 ] had to be monitored constantly\n• tracking devices were fitted to the matriarchs\n• data including the size of their tusks and 37 ________ was taken\n• elephants were taken by truck to their new reserve",
                options = null,
                sectionNumber = 4,
                timestampStart = 289,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_38",
                position = 38,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nAdvantages of translocation at Nkhotakota Wildlife Park\n• 38 ________ opportunities\n• a reduction in the number of poachers and [ 39 ]\n• an example of conservation that other parks can follow",
                options = null,
                sectionNumber = 4,
                timestampStart = 316,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_39",
                position = 39,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nAdvantages of translocation at Nkhotakota Wildlife Park\n• [ 38 ] opportunities\n• a reduction in the number of poachers and 39 ________\n• an example of conservation that other parks can follow",
                options = null,
                sectionNumber = 4,
                timestampStart = 336,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "q_40",
                position = 40,
                type = "FILL_IN_BLANK",
                prompt = "Elephant translocation\nAdvantages of translocation at Nkhotakota Wildlife Park\n• a reduction in the number of poachers and [ 39 ]\n• an example of conservation that other parks can follow\n• an increase in 40 ________ as a contributor to GDP",
                options = null,
                sectionNumber = 4,
                timestampStart = 381,
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

    private fun getCam18Test2Detail(testItem: com.example.ielts.data.remote.dto.ListeningTestListItemDto): ListeningTestDetailDto {
        val sections = listOf(
            ListeningSectionDto(
                sectionNumber = 1,
                title = "Part 1: Working at Milo's Restaurants",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758609/%E5%89%9118_section2-part1.mp3",
                durationSeconds = 480,
                transcript = "[00:33] ANNOUNCER: Part 1. You will hear a woman from a job agency giving information to a man about work in a chain of restaurants.\n[00:41] ANNOUNCER: First, you have some time to look at questions 1 to 5.\n[01:13] ANNOUNCER: Now listen carefully and answer questions 1 to 5.\n[01:23] WOMAN: So I understand you're interested in restaurant work?\n[01:27] MAN: Yes, I've got a bit of experience and I can provide references.\n[01:31] WOMAN: That's good. I can check all that later. Now, Milo's restaurants have some vacancies at the moment. They're a really good company to work for. Lots of benefits.\n[01:39] WOMAN: They've got a very good reputation for looking after staff. For example, all employees get training, even temporary staff. [Q1]\n[01:48] MAN: Oh really? That's quite unusual, isn't it?\n[01:51] WOMAN: Certainly is.\n[01:52] MAN: And do staff get free uniforms too?\n[01:55] WOMAN: Erm, you just need to wear a white t-shirt and black trousers, it says here, so I guess not. But another benefit of working for a big company like this is that you can get a discount at any of their restaurants. [Q2]\n[02:04] MAN: Even at weekends?\n[02:05] WOMAN: No, but you'll be working then anyway.\n[02:08] MAN: Oh yes, I suppose so. Most of their restaurants are in the city centre, aren't they? So easy to get to by bus?\n[02:16] WOMAN: Yes, that's right. But if you have to do a late shift and finish work after midnight, the company will pay for you to get a taxi home. [Q3]\n[02:30] MAN: I probably won't need one. I think I'd use my bike.\n[02:36] WOMAN: OK. Now, they do have some quite specific requirements for the kind of person they're looking for. Milo's is a young, dynamic company and they're really keen on creating a strong team.\n[02:48] WOMAN: It's really important that you can fit in and get on well with everyone.\n[02:52] MAN: Yep, I've got no problem with that. It sounds good actually. The last place I worked for was quite demanding too. We had to make sure we gave a really high level of service. [Q4]\n[03:04] WOMAN: That's good to hear, because that will be equally important at Milo's. I know they want people who have an eye for detail.\n[03:15] MAN: That's fine. I'm very used to working in that kind of environment.\n[03:24] WOMAN: Perfect. So the only other thing that's required is good communication skills, so you'll need to have a certificate in English. [Q5]\n[03:33] MAN: Sure.\n[03:36] ANNOUNCER: Before you hear the rest of the conversation, you have some time to look at questions 6 to 10.\n[04:07] ANNOUNCER: Now listen and answer questions 6 to 10.\n[04:14] WOMAN: OK. Let's have a look at the current job vacancies at Milo's. The first one is in Wivenhoe Street. [Q6]\n[04:21] MAN: Sorry, where?\n[04:23] WOMAN: Wivenhoe. W-I-V-E-N-H-O-E. It's quite central, just off Cork Street.\n[04:43] MAN: Oh, right.\n[04:44] WOMAN: They're looking for a breakfast supervisor.\n[04:46] MAN: That would be OK.\n[04:48] WOMAN: So you're probably familiar with the kind of responsibilities involved. Obviously checking that all the portions are correct, etc. And then things like checking all the procedures for cleaning the equipment are being followed. [Q7]\n[05:01] MAN: OK. And what about the salary? In my last job, I was getting £9.50 per hour. I was hoping to get a bit more than that.\n[05:16] WOMAN: Well, to begin with, you'd be getting £9.75, but that goes up to £11.25 after three months. [Q8]\n[05:30] MAN: That's not too bad. And I suppose it's a very early start.\n[05:35] WOMAN: That's the only unattractive thing about this job. But then you have the afternoons and evenings free. So the restaurant starts serving breakfast from 7am and you'd have to be there at 5.30 to set everything up. But you'd be finished at 12.30.\n[05:42] MAN: Well, as you say, there are advantages to that.\n[05:46] WOMAN: Now, you might also be interested in the job at the City Road branch. That's for a junior chef. So again, a position of responsibility.\n[05:59] MAN: I might prefer that, actually.\n[06:01] WOMAN: Right. Well, obviously, this role would involve supporting the sous chef and other senior staff. And you'd be responsible for making sure there's enough stock each week and sorting out all the deliveries. [Q9]\n[06:44] MAN: I've never done that before, but I imagine it's fairly straightforward once you get the hang of it.\n[06:56] WOMAN: Yes. And you'd be working alongside more experienced staff to begin with, so I'm sure it wouldn't be a problem. The salary's slightly higher here. It's an annual salary of £23,000.\n[07:09] MAN: Right.\n[07:10] WOMAN: I know that if they like you, it's likely you'll be promoted quite quickly, so that's worth thinking about.\n[07:17] MAN: Yes, it does sound interesting. What are the hours like?\n[07:21] WOMAN: The usual, I think. There's a lot of evening and weekend work, but they're closed on Mondays. But you do get one Sunday off every four weeks, so would you like me to send off your CV? [Q10]"
            ),
            ListeningSectionDto(
                sectionNumber = 2,
                title = "Part 2: Planned Housing Development",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758609/%E5%89%9118_section2-part2.mp3",
                durationSeconds = 430,
                transcript = "[00:00] ANNOUNCER: Part 2. You will hear a talk to members of the public about plans for a new housing development near a town called Nunston.\n[00:11] ANNOUNCER: First, you have some time to look at questions 11 to 14.\n[00:35] ANNOUNCER: Now listen carefully and answer questions 11 to 14.\n[00:43] MARK REYNOLDS: Hello everyone, it's good to see that so many members of the public have shown up for our presentation on the new housing development planned on the outskirts of Nunston.\n[00:54] MARK REYNOLDS: I'm Mark Reynolds and I'm Communications Manager at the development.\n[01:01] MARK REYNOLDS: I'll start by giving you a brief overview of our plans for the development.\n[01:07] MARK REYNOLDS: So, one thing I'm sure you'll want to know is why we've selected this particular site for a housing development. At present, it's being used for farming, like much of the land around Nunston.\n[01:19] MARK REYNOLDS: But because of the new industrial centre in Nunston, there's a lot of demand for housing for employees in the region, as many employees are having to commute long distances at present. [Q11-12]\n[01:37] MARK REYNOLDS: Of course, there's also the fact that we have an international airport just 20 minutes drive away. But, although that's certainly convenient, it wasn't one of our major criteria for choosing the site.\n[01:52] MARK REYNOLDS: We were more interested in the fact that there's an excellent hospital just 15 km away, and a large secondary school even closer than that. [Q11-12]\n[02:05] MARK REYNOLDS: One drawback to the site is that it's on quite a steep slope, but we've taken account of that in our planning, so it shouldn't be a major problem.\n[02:19] MARK REYNOLDS: We've had a lot of positive feedback about the plans. People like the wide variety of accommodation types and prices, and the fact that it's only a short drive to get out into the countryside from the development.\n[02:39] MARK REYNOLDS: We were particularly pleased that so many people liked the designs for the layout of the development, with the majority of people saying it generally made a good impression and blended in well with the natural features of the landscape, with provision made for protecting trees and wildlife on the site. [Q13-14]\n[02:59] MARK REYNOLDS: Some people have mentioned that they'd like to see more facilities for cyclists, and we'll look at that, but the overall feedback has been that the design and facilities of the development make it seem a place where people of all ages can live together happily. [Q13-14]\n[03:21] ANNOUNCER: Before you hear the rest of the talk, you have some time to look at questions 15 to 20.\n[04:02] ANNOUNCER: Now listen and answer questions 15 to 20.\n[04:09] MARK REYNOLDS: OK, so I'll put a map of the proposed development up on the screen. You'll see it's bounded on the south side by the main road, which then goes on to Nunston.\n[04:28] MARK REYNOLDS: Another boundary is formed by London Road, on the western side of the development.\n[04:35] MARK REYNOLDS: Inside the development there'll be about 400 houses and three apartment blocks. There'll also be a school for children up to 11 years old.\n[04:51] MARK REYNOLDS: If you look at the south entrance at the bottom of the map, there's a road from there that goes right up through the development.\n[04:59] MARK REYNOLDS: The school will be on that road, at the corner of the second turning to the left. [Q15]\n[05:08] MARK REYNOLDS: A large sports centre is planned with facilities for indoor and outdoor activities. This will be on the western side of the development, just below the road that branches off from London Road. [Q16]\n[05:25] MARK REYNOLDS: There'll be a clinic where residents can go if they have any health problems. Can you see the lake towards the top of the map?\n[05:33] MARK REYNOLDS: The clinic will be just below this, to the right of a street of houses. [Q17]\n[05:40] MARK REYNOLDS: There'll also be a community centre for people of all ages. On the northeast side of the development there'll be a row of specially designed houses, specifically for residents over 65, and the community centre will be adjoining this. [Q18]\n[05:55] MARK REYNOLDS: We haven't forgotten about shopping. There'll be a supermarket between the two entrances to the development.\n[06:02] MARK REYNOLDS: We're planning to leave the three large trees near London Road, and it'll be just to the south of these. [Q19]\n[06:10] MARK REYNOLDS: It's planned to have a playground for younger children. If you look at the road that goes up from the south entrance, you'll see it curves round to the left at the top, and the playground will be in that curve, with nice views of the lake. [Q20]\n[06:41] MARK REYNOLDS: Okay, so, now, does anyone... That is the end of part two. You now have 30 seconds to check your answers to part two."
            ),
            ListeningSectionDto(
                sectionNumber = 3,
                title = "Part 3: The Laki Eruption of 1783",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758609/%E5%89%9118_section2-part1.mp3",
                durationSeconds = 420,
                transcript = "[00:00] ANNOUNCER: Part 3. You will hear two geography students, Adam and Michelle, talking about a volcanic eruption which took place in Iceland in 1783.\n[00:08] ANNOUNCER: First, you have some time to look at questions 21 to 26.\n[00:58] ANNOUNCER: Now listen carefully and answer questions 21 to 26.\n[01:06] ADAM: So, Michelle, shall we make a start on our presentation? We haven't got that much time left.\n[01:12] MICHELLE: No, Adam, but at least we've done all the background reading. I found it really interesting. I'd never even heard of the Laki eruption before this.\n[01:22] ADAM: Me neither. I suppose 1783 is a long time ago.\n[01:26] MICHELLE: But it was a huge eruption and it had such devastating consequences.\n[01:31] ADAM: I know. It was great there were so many primary sources to look at. It really gives you a sense of how catastrophic the volcano was. People were really trying to make sense of the science for the first time.\n[01:46] MICHELLE: That's right. But what I found more significant was how it impacted directly and indirectly on political events, as well as having massive social and economic consequences. [Q21]\n[01:57] ADAM: I know. That should be the main focus of our presentation. The observations made by people at the time were interesting, weren't they? I mean, they all gave a pretty consistent account of what happened, even if they didn't always use the same terminology.\n[02:14] MICHELLE: Yeah. I was surprised there were so many weather stations established by that time. [Q22]\n[02:20] ADAM: So, you know, you can see how the weather changed often by the hour.\n[02:24] MICHELLE: Right. Writers at the time talked about the Laki haze to describe the volcanic fog that spread across Europe. They all realised that this wasn't the sort of fog they were used to.\n[02:36] ADAM: And, of course, this was in pre-industrial times, so they hadn't experienced sulphur-smelling fog before.\n[02:43] MICHELLE: No, that's true. Reports from the period blamed the haze for an increase in headaches, respiratory issues and asthma attacks. And they all describe how it covered the sun and made it look a strange red colour. [Q23]\n[02:57] ADAM: Must have been very weird. It's interesting that Benjamin Franklin wrote about the haze. Did you read that? He was the American ambassador in Paris at the time.\n[03:09] MICHELLE: Yeah. At first no one realised that the haze was caused by the volcanic eruption in Iceland.\n[03:16] ADAM: It was Benjamin Franklin who realised that before anyone else. He's often credited with that, apparently.\n[03:20] MICHELLE: But a French naturalist beat him to it. I can't remember his name. I'd have to look it up. [Q24]\n[03:28] ADAM: Then other naturalists had the same idea. All independently of each other.\n[03:34] MICHELLE: Oh, right. We should talk about the immediate impact of the eruption, which was obviously enormous, especially in Iceland where so many people died.\n[03:46] ADAM: You'd expect that. And the fact that the volcanic ash drifted so swiftly, but not that the effects would go on for so long. Or that two years after the eruption, strange weather events were being reported as far away as North America and North Africa. [Q25-26]\n[04:02] MICHELLE: No. I found all that hard to believe too. It must have been terrible. And there was nothing anyone could do about it, even if they knew the ash cloud was coming in their direction. [Q25-26]\n[04:19] ANNOUNCER: Before you hear the rest of the discussion, you have some time to look at questions 27 to 30.\n[04:45] ANNOUNCER: Now listen and answer questions 27 to 30.\n[04:52] ADAM: We should run through some of the terrible consequences of the eruption experienced in different countries. There's quite a varied range.\n[05:01] MICHELLE: Starting with Iceland, where the impact on farming was devastating. One of the most dramatic things there was the effect on livestock as they grazed in the fields.\n[05:13] ADAM: They were poisoned because they ate vegetation that had been contaminated with fluorine as a result of the volcanic fallout. [Q27]\n[05:18] MICHELLE: That was horrible. In Egypt, the bizarre weather patterns led to a severe drought.\n[05:24] ADAM: And as a result, the Nile didn't flood, which meant the crops all failed.\n[05:30] MICHELLE: It's so far from where the eruption happened. And yet the famine there led to more people dying than any other country. [Q28]\n[05:41] ADAM: It was worse than the plague. OK. Then in the UK, the mortality rate went up a lot, presumably from respiratory illnesses.\n[05:53] ADAM: According to one report, it was about double the usual number and included an unusually high percentage of people under the age of 25. [Q29]\n[06:03] MICHELLE: Hmm. I think people will be surprised to hear that the weather in the USA was badly affected too.\n[06:08] ADAM: George Washington even makes a note in his diary that they were snowbound until March in Virginia. That was before he became President. [Q30]\n[06:14] MICHELLE: Yes. And there was ice floating down the Mississippi, which was unprecedented. [Q30]\n[06:21] ADAM: Oh, astonishing really. Anyway, what do you think we should include next?\n[06:25] ANNOUNCER: That is the end of part three. You now have 30 seconds to check your answers to part three."
            ),
            ListeningSectionDto(
                sectionNumber = 4,
                title = "Part 4: Pockets",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758609/%E5%89%9118_section2-part1.mp3",
                durationSeconds = 380,
                transcript = "[00:00] Lecturer: In today's design history lecture, we shall investigate the evolution of pockets in clothing and their societal implications."
            )
        )

                val questions = listOf(
            // ==================== PART 1: Working at Milo's Restaurants ====================
            ListeningQuestionDetailDto(
                id = "cam18_t2_q1",
                position = 1,
                type = "FILL_IN_BLANK",
                prompt = "Working at Milo's Restaurants\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nBenefits\n• 1 ________ provided for all staff\n• [ 2 ] during weekdays at all Milo's Restaurants\n• [ 3 ] provided after midnight\n\nPerson specification\n• must be prepared to work well in a team\n• must care about maintaining a high standard of [ 4 ]\n• must have a qualification in [ 5 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 99,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_notes,
                imageTitle = "Ghi chú: Working at Milo's Restaurants"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q2",
                position = 2,
                type = "FILL_IN_BLANK",
                prompt = "Working at Milo's Restaurants\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nBenefits\n• [ 1 ] provided for all staff\n• 2 ________ during weekdays at all Milo's Restaurants\n• [ 3 ] provided after midnight\n\nPerson specification\n• must be prepared to work well in a team\n• must care about maintaining a high standard of [ 4 ]\n• must have a qualification in [ 5 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 115,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_notes,
                imageTitle = "Ghi chú: Working at Milo's Restaurants"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q3",
                position = 3,
                type = "FILL_IN_BLANK",
                prompt = "Working at Milo's Restaurants\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nBenefits\n• [ 1 ] provided for all staff\n• [ 2 ] during weekdays at all Milo's Restaurants\n• 3 ________ provided after midnight\n\nPerson specification\n• must be prepared to work well in a team\n• must care about maintaining a high standard of [ 4 ]\n• must have a qualification in [ 5 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 136,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_notes,
                imageTitle = "Ghi chú: Working at Milo's Restaurants"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q4",
                position = 4,
                type = "FILL_IN_BLANK",
                prompt = "Working at Milo's Restaurants\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nBenefits\n• [ 1 ] provided for all staff\n• [ 2 ] during weekdays at all Milo's Restaurants\n• [ 3 ] provided after midnight\n\nPerson specification\n• must be prepared to work well in a team\n• must care about maintaining a high standard of 4 ________\n• must have a qualification in [ 5 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 172,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_notes,
                imageTitle = "Ghi chú: Working at Milo's Restaurants"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q5",
                position = 5,
                type = "FILL_IN_BLANK",
                prompt = "Working at Milo's Restaurants\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nBenefits\n• [ 1 ] provided for all staff\n• [ 2 ] during weekdays at all Milo's Restaurants\n• [ 3 ] provided after midnight\n\nPerson specification\n• must be prepared to work well in a team\n• must care about maintaining a high standard of [ 4 ]\n• must have a qualification in 5 ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 204,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_notes,
                imageTitle = "Ghi chú: Working at Milo's Restaurants"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q6",
                position = 6,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: 6 ________ Street\nJob title: Breakfast supervisor\nResponsibilities include:\n• Checking portions, etc. are correct\n• Making sure [ 7 ] is clean\nPay and conditions:\n• Starting salary: £ [ 8 ] per hour\n• Start work at 5.30 a.m.",
                options = null,
                sectionNumber = 1,
                timestampStart = 254,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_table,
                imageTitle = "Bảng tuyển dụng (Questions 6–10)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q7",
                position = 7,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: [ 6 ] Street\nJob title: Breakfast supervisor\nResponsibilities include:\n• Checking portions, etc. are correct\n• Making sure 7 ________ is clean\nPay and conditions:\n• Starting salary: £ [ 8 ] per hour\n• Start work at 5.30 a.m.",
                options = null,
                sectionNumber = 1,
                timestampStart = 288,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_table,
                imageTitle = "Bảng tuyển dụng (Questions 6–10)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q8",
                position = 8,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: [ 6 ] Street\nJob title: Breakfast supervisor\nResponsibilities include:\n• Checking portions, etc. are correct\n• Making sure [ 7 ] is clean\nPay and conditions:\n• Starting salary: £ 8 ________ per hour\n• Start work at 5.30 a.m.",
                options = null,
                sectionNumber = 1,
                timestampStart = 316,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_table,
                imageTitle = "Bảng tuyển dụng (Questions 6–10)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q9",
                position = 9,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: City Road\nJob title: Junior chef\nResponsibilities include:\n• Supporting senior chefs\n• Maintaining stock and organising 9 ________\nPay and conditions:\n• Annual salary £23,000\n• No work on a [ 10 ] once a month",
                options = null,
                sectionNumber = 1,
                timestampStart = 361,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_table,
                imageTitle = "Bảng tuyển dụng (Questions 6–10)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q10",
                position = 10,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: City Road\nJob title: Junior chef\nResponsibilities include:\n• Supporting senior chefs\n• Maintaining stock and organising [ 9 ]\nPay and conditions:\n• Annual salary £23,000\n• No work on a 10 ________ once a month",
                options = null,
                sectionNumber = 1,
                timestampStart = 441,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part1_table,
                imageTitle = "Bảng tuyển dụng (Questions 6–10)"
            ),

            // ==================== PART 2: Planned Housing Development ====================
            ListeningQuestionDetailDto(
                id = "cam18_t2_q11",
                position = 11,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 11 and 12: Choose TWO letters, A–E.\nWhat are the TWO main reasons why this site has been chosen for the housing development?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf(
                    "A. It has suitable geographical features.",
                    "B. There is easy access to local facilities.",
                    "C. It has good connections with the airport.",
                    "D. The land is of little agricultural value.",
                    "E. It will be convenient for workers."
                ),
                sectionNumber = 2,
                timestampStart = 79,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q12",
                position = 12,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 11 and 12: Choose TWO letters, A–E.\nWhat are the TWO main reasons why this site has been chosen for the housing development?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf(
                    "A. It has suitable geographical features.",
                    "B. There is easy access to local facilities.",
                    "C. It has good connections with the airport.",
                    "D. The land is of little agricultural value.",
                    "E. It will be convenient for workers."
                ),
                sectionNumber = 2,
                timestampStart = 112,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q13",
                position = 13,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 13 and 14: Choose TWO letters, A–E.\nWhich TWO aspects of the planned housing development have people given positive feedback about?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf(
                    "A. the facilities for cyclists",
                    "B. the impact on the environment",
                    "C. the encouragement of good relations between residents",
                    "D. the low cost of all the accommodation",
                    "E. the rural location"
                ),
                sectionNumber = 2,
                timestampStart = 159,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q14",
                position = 14,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 13 and 14: Choose TWO letters, A–E.\nWhich TWO aspects of the planned housing development have people given positive feedback about?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf(
                    "A. the facilities for cyclists",
                    "B. the impact on the environment",
                    "C. the encouragement of good relations between residents",
                    "D. the low cost of all the accommodation",
                    "E. the rural location"
                ),
                sectionNumber = 2,
                timestampStart = 179,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q15",
                position = 15,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Label the map below.\nWrite the correct letter, A–I, next to Questions 15–20.\n\n15. School",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I"),
                sectionNumber = 2,
                timestampStart = 299,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part2_map,
                imageTitle = "Bản đồ Planned Housing Development (A–I)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q16",
                position = 16,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Label the map below.\nWrite the correct letter, A–I, next to Questions 15–20.\n\n16. Sports centre",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I"),
                sectionNumber = 2,
                timestampStart = 308,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part2_map,
                imageTitle = "Bản đồ Planned Housing Development (A–I)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q17",
                position = 17,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Label the map below.\nWrite the correct letter, A–I, next to Questions 15–20.\n\n17. Clinic",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I"),
                sectionNumber = 2,
                timestampStart = 333,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part2_map,
                imageTitle = "Bản đồ Planned Housing Development (A–I)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q18",
                position = 18,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Label the map below.\nWrite the correct letter, A–I, next to Questions 15–20.\n\n18. Community centre",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I"),
                sectionNumber = 2,
                timestampStart = 340,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part2_map,
                imageTitle = "Bản đồ Planned Housing Development (A–I)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q19",
                position = 19,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Label the map below.\nWrite the correct letter, A–I, next to Questions 15–20.\n\n19. Supermarket",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I"),
                sectionNumber = 2,
                timestampStart = 362,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part2_map,
                imageTitle = "Bản đồ Planned Housing Development (A–I)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q20",
                position = 20,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Label the map below.\nWrite the correct letter, A–I, next to Questions 15–20.\n\n20. Playground",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I"),
                sectionNumber = 2,
                timestampStart = 370,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part2_map,
                imageTitle = "Bản đồ Planned Housing Development (A–I)"
            ),

            // ==================== PART 3: The Laki Eruption of 1783 ====================
            ListeningQuestionDetailDto(
                id = "cam18_t2_q21",
                position = 21,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 21–24: Choose the correct letter, A, B or C.\n\n21. Why do the students think the Laki eruption of 1783 is so important?",
                options = listOf(
                    "A. It was the most severe eruption in modern times.",
                    "B. It led to the formal study of volcanoes.",
                    "C. It had a profound effect on society."
                ),
                sectionNumber = 3,
                timestampStart = 106,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q22",
                position = 22,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 21–24: Choose the correct letter, A, B or C.\n\n22. What surprised Adam about observations made at the time?",
                options = listOf(
                    "A. the number of places producing them",
                    "B. the contradictions in them",
                    "C. the lack of scientific data to support them"
                ),
                sectionNumber = 3,
                timestampStart = 134,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q23",
                position = 23,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 21–24: Choose the correct letter, A, B or C.\n\n23. According to Michelle, what did the contemporary sources say about the Laki haze?",
                options = listOf(
                    "A. People thought it was similar to ordinary fog.",
                    "B. It was associated with health issues.",
                    "C. It completely blocked out the sun for weeks."
                ),
                sectionNumber = 3,
                timestampStart = 163,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q24",
                position = 24,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 21–24: Choose the correct letter, A, B or C.\n\n24. Adam corrects Michelle when she claims that Benjamin Franklin",
                options = listOf(
                    "A. came to the wrong conclusion about the cause of the haze.",
                    "B. was the first to identify the reason for the haze.",
                    "C. supported the opinions of other observers about the haze."
                ),
                sectionNumber = 3,
                timestampStart = 200,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q25",
                position = 25,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 25 and 26: Choose TWO letters, A–E.\nWhich TWO issues following the Laki eruption surprised the students?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf(
                    "A. how widespread the effects were",
                    "B. how long-lasting the effects were",
                    "C. the number of deaths it caused",
                    "D. the speed at which the volcanic ash cloud spread",
                    "E. how people ignored the warning signs"
                ),
                sectionNumber = 3,
                timestampStart = 226,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q26",
                position = 26,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 25 and 26: Choose TWO letters, A–E.\nWhich TWO issues following the Laki eruption surprised the students?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf(
                    "A. how widespread the effects were",
                    "B. how long-lasting the effects were",
                    "C. the number of deaths it caused",
                    "D. the speed at which the volcanic ash cloud spread",
                    "E. how people ignored the warning signs"
                ),
                sectionNumber = 3,
                timestampStart = 242,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q27",
                position = 27,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 27–30\nWhat comment do the students make about the impact of the Laki eruption on the following countries?\nChoose FOUR answers from the box and write the correct letter, A–F, next to Questions 27–30.\n\nComments:\nA. This country suffered the most severe loss of life.\nB. The impact on agriculture was predictable.\nC. There was a significant increase in deaths of young people.\nD. Animals suffered from a sickness.\nE. This country saw the highest rise in food prices in the world.\nF. It caused a particularly harsh winter.\n\n27. Iceland",
                options = listOf(
                    "A. This country suffered the most severe loss of life.",
                    "B. The impact on agriculture was predictable.",
                    "C. There was a significant increase in deaths of young people.",
                    "D. Animals suffered from a sickness.",
                    "E. This country saw the highest rise in food prices in the world.",
                    "F. It caused a particularly harsh winter."
                ),
                sectionNumber = 3,
                timestampStart = 313,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part3_comments,
                imageTitle = "Bảng ý kiến: Comments A–F (Questions 27–30)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q28",
                position = 28,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 27–30\nWhat comment do the students make about the impact of the Laki eruption on the following countries?\nChoose FOUR answers from the box and write the correct letter, A–F, next to Questions 27–30.\n\nComments:\nA. This country suffered the most severe loss of life.\nB. The impact on agriculture was predictable.\nC. There was a significant increase in deaths of young people.\nD. Animals suffered from a sickness.\nE. This country saw the highest rise in food prices in the world.\nF. It caused a particularly harsh winter.\n\n28. Egypt",
                options = listOf(
                    "A. This country suffered the most severe loss of life.",
                    "B. The impact on agriculture was predictable.",
                    "C. There was a significant increase in deaths of young people.",
                    "D. Animals suffered from a sickness.",
                    "E. This country saw the highest rise in food prices in the world.",
                    "F. It caused a particularly harsh winter."
                ),
                sectionNumber = 3,
                timestampStart = 330,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part3_comments,
                imageTitle = "Bảng ý kiến: Comments A–F (Questions 27–30)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q29",
                position = 29,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 27–30\nWhat comment do the students make about the impact of the Laki eruption on the following countries?\nChoose FOUR answers from the box and write the correct letter, A–F, next to Questions 27–30.\n\nComments:\nA. This country suffered the most severe loss of life.\nB. The impact on agriculture was predictable.\nC. There was a significant increase in deaths of young people.\nD. Animals suffered from a sickness.\nE. This country saw the highest rise in food prices in the world.\nF. It caused a particularly harsh winter.\n\n29. UK",
                options = listOf(
                    "A. This country suffered the most severe loss of life.",
                    "B. The impact on agriculture was predictable.",
                    "C. There was a significant increase in deaths of young people.",
                    "D. Animals suffered from a sickness.",
                    "E. This country saw the highest rise in food prices in the world.",
                    "F. It caused a particularly harsh winter."
                ),
                sectionNumber = 3,
                timestampStart = 353,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part3_comments,
                imageTitle = "Bảng ý kiến: Comments A–F (Questions 27–30)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q30",
                position = 30,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 27–30\nWhat comment do the students make about the impact of the Laki eruption on the following countries?\nChoose FOUR answers from the box and write the correct letter, A–F, next to Questions 27–30.\n\nComments:\nA. This country suffered the most severe loss of life.\nB. The impact on agriculture was predictable.\nC. There was a significant increase in deaths of young people.\nD. Animals suffered from a sickness.\nE. This country saw the highest rise in food prices in the world.\nF. It caused a particularly harsh winter.\n\n30. USA",
                options = listOf(
                    "A. This country suffered the most severe loss of life.",
                    "B. The impact on agriculture was predictable.",
                    "C. There was a significant increase in deaths of young people.",
                    "D. Animals suffered from a sickness.",
                    "E. This country saw the highest rise in food prices in the world.",
                    "F. It caused a particularly harsh winter."
                ),
                sectionNumber = 3,
                timestampStart = 368,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part3_comments,
                imageTitle = "Bảng ý kiến: Comments A–F (Questions 27–30)"
            ),

            // ==================== PART 4: Pockets ====================
            ListeningQuestionDetailDto(
                id = "cam18_t2_q31",
                position = 31,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nReason for choice of subject\n• They are 31 ________ but can be overlooked by consumers and designers.",
                options = null,
                sectionNumber = 4,
                timestampStart = 87,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q32",
                position = 32,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nPockets in men's clothes\n• Men started to wear 32 ________ in the 18th century.\n• A [ 33 ] sewed pockets into the lining of the garments.",
                options = null,
                sectionNumber = 4,
                timestampStart = 125,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q33",
                position = 33,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nPockets in men's clothes\n• Men started to wear [ 32 ] in the 18th century.\n• A 33 ________ sewed pockets into the lining of the garments.\n• The wearer could use the pockets for small items.",
                options = null,
                sectionNumber = 4,
                timestampStart = 150,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q34",
                position = 34,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nPockets in men's clothes\n• A [ 33 ] sewed pockets into the lining of the garments.\n• The wearer could use the pockets for small items.\n• Bigger pockets might be made for men who belonged to a certain type of 34 ________",
                options = null,
                sectionNumber = 4,
                timestampStart = 170,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q35",
                position = 35,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nPockets in women's clothes\n• Women's pockets were less 35 ________ than men's.\n• Women were very concerned about pickpockets.",
                options = null,
                sectionNumber = 4,
                timestampStart = 193,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q36",
                position = 36,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nPockets in women's clothes\n• Women were very concerned about pickpockets.\n• Pockets were produced in pairs using 36 ________ to link them together.\n• Pockets hung from the women's [ 37 ] under skirts and petticoats.",
                options = null,
                sectionNumber = 4,
                timestampStart = 223,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q37",
                position = 37,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nPockets in women's clothes\n• Pockets were produced in pairs using [ 36 ] to link them together.\n• Pockets hung from the women's 37 ________ under skirts and petticoats.\n• Items such as [ 38 ] could be reached through a gap in the material.",
                options = null,
                sectionNumber = 4,
                timestampStart = 245,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q38",
                position = 38,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nPockets in women's clothes\n• Pockets hung from the women's [ 37 ] under skirts and petticoats.\n• Items such as 38 ________ could be reached through a gap in the material.\n• Pockets, of various sizes, stayed inside clothing for many decades.",
                options = null,
                sectionNumber = 4,
                timestampStart = 261,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q39",
                position = 39,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nPockets in women's clothes\n• Pockets, of various sizes, stayed inside clothing for many decades.\n• When dresses changed shape, hidden pockets had a negative effect on the 39 ________ of women.\n• Bags called 'pouches' became popular, before women carried a [ 40 ]",
                options = null,
                sectionNumber = 4,
                timestampStart = 309,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q40",
                position = 40,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nPockets in women's clothes\n• When dresses changed shape, hidden pockets had a negative effect on the [ 39 ] of women.\n• Bags called 'pouches' became popular, before women carried a 40 ________",
                options = null,
                sectionNumber = 4,
                timestampStart = 325,
                timestampEnd = 0,
                imageResId = R.drawable.img_cam18_test2_part4_pockets,
                imageTitle = "Bảng ghi chú: Pockets (Questions 31–40)"
            )
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


    private fun getCam18Test3Detail(testItem: com.example.ielts.data.remote.dto.ListeningTestListItemDto): ListeningTestDetailDto {
        val sections = listOf(
            ListeningSectionDto(
                sectionNumber = 1,
                title = "Part 1: Wayside Camera Club",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758712/%E5%89%9118_section3_part1.mp3",
                durationSeconds = 405,
                transcript = "[00:36] ANNOUNCER: Introduction. A man is making a phone call to a photography club because he is interested in becoming a member.\n[01:15] WOMAN: Hello, Wayside Camera Club. Brida speaking. How can I help you?\n[01:18] DAN: Hello. My name is Dan Green. I'm calling because I'd like to become a member of your club.\n[01:23] WOMAN: Certainly. I can help you complete the registration over the phone. I'll just need to ask you a few questions first.\n[01:32] WOMAN: Could I have your surname, please?\n[01:36] DAN: It's Green.\n[01:39] WOMAN: Thank you. And what is your email address?\n[01:42] DAN: He provides his email address.\n[01:49] WOMAN: Could you also tell me your home address?\n[01:53] DAN: I live in Peacetown, which is approximately ten miles from the club. I live in a house.\n[02:00] WOMAN: Right. What's the number of your house and the name of the street?\n[02:04] DAN: It's number 52, Marrowfield Street. [Q1]\n[02:07] WOMAN: Could you spell the street name for me? I want to make sure I've written it correctly.\n[02:19] DAN: He confirms the spelling.\n[02:20] WOMAN: Now, how did you first hear about our camera club? Was it through the Internet?\n[02:26] DAN: Actually, a relative told me about it. [Q2]\n[02:37] WOMAN: Is that relative already a member of our club?\n[02:39] DAN: No. He belongs to a different photography club, but he has heard positive things about yours.\n[02:44] WOMAN: I see. And what made you interested in joining us?\n[02:48] DAN: I'm particularly interested in taking part in the photography competitions organised by the club.\n[02:56] WOMAN: Is there anything else that attracts you to the club?\n[02:58] DAN: I'd also like to meet other people who share my interest in photography. [Q3]\n[03:02] WOMAN: That's good. We offer two types of membership. Full membership costs £30 per year, while associate membership is £20 annually.\n[03:15] DAN: I think I'd prefer the full membership. [Q4]\n[03:19] WOMAN: That's fine. Associate members can attend activities, but they don't have the right to vote at club meetings. Now, I'd like to ask you a little about your previous photography experience.\n[04:11] WOMAN: Have you ever entered any photography competitions before?\n[04:15] DAN: Yes, I've entered three competitions, although I haven't actually won any of them.\n[04:26] WOMAN: Could you tell me about the first competition?\n[04:30] DAN: The theme was Domestic Life. I submitted a photograph of a family sitting around a table and having dinner together. [Q5]\n[04:38] WOMAN: What sort of comments did the judges make about your photograph?\n[04:42] DAN: They said that the picture was rather cluttered. There were too many things in the photograph, so the main subject wasn't clear enough.\n[04:51] WOMAN: And what about the second competition?\n[04:55] DAN: It was held near the university, in a coastal area. The subject was sunsets, so I decided to take a picture of the sun going down.\n[05:23] DAN: The judges felt that the photograph would have been better if there had been some clouds in the sky. The picture mainly showed a blue sky and the setting sun. [Q6]\n[05:40] DAN: They also thought I'd taken the photograph too early. I should have waited longer to capture the most impressive moment. [Q7]\n[05:55] WOMAN: What was the theme of your third competition?\n[05:59] DAN: It was called Animal Magic. There were quite a lot of photographs of animals, which can be difficult because animals don't always stay still. [Q8] [Q9]\n[06:15] DAN: I managed to take a nice photograph of a fox, but the problem was that the picture was taken at night and it came out too dark. [Q10]\n[06:28] WOMAN: Well, you certainly seem enthusiastic about photography. We're very pleased to have you joining the club. I'm sure you'll have plenty of opportunities to develop your skills here.\n[06:38] ANNOUNCER: End of Part 1."
            ),
            ListeningSectionDto(
                sectionNumber = 2,
                title = "Part 2: Picking Mushrooms",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758712/%E5%89%9118_section3_part1.mp3",
                durationSeconds = 440,
                transcript = "[00:00] ANNOUNCER: Introduction. A radio presenter introduces an expert who will talk about collecting wild mushrooms and explain some important points about mushroom foraging.\n[00:40] PRESENTER: This evening, we're talking about picking wild mushrooms. Our guest is Dan Beagle, who has written a book about foraging. He's here to share some useful advice with us.\n[00:59] DAN: The first thing I want to emphasise is safety. Some wild mushrooms are poisonous, and it is essential to know how to recognise them before collecting anything.\n[01:14] DAN: However, mushroom foraging can be a very rewarding activity. There are many different types of wild mushrooms, including varieties that you simply cannot find in ordinary shops.\n[01:22] DAN: You should never eat mushrooms collected by friends or neighbours unless you can identify them yourself. Some poisonous species look very similar to edible ones. You should also avoid collecting mushrooms growing close to busy roads. [Q11-12]\n[01:51] DAN: Another advantage of picking mushrooms yourself is their freshness. Wild mushrooms can taste much better than those sold in shops, where they may have been sitting for several days.\n[02:03] PRESENTER: Some people believe that only pale-coloured mushrooms are safe to eat. Is that true?\n[02:08] DAN: No, that's a common misunderstanding. Some mushrooms that are perfectly safe to eat have bright colours, including red ones. Colour alone cannot tell you whether a mushroom is poisonous. [Q13-14]\n[02:26] DAN: Mushrooms can be prepared in different ways. You can cook them or add them raw to salads. You don't normally need to remove their skins. Also, don't assume that a mushroom is safe simply because it smells pleasant.\n[02:45] DAN: Some poisonous mushrooms have no unusual smell at all. In fact, certain dangerous varieties can even taste quite pleasant. You should never use smell or taste as a method of checking whether a mushroom is safe.\n[02:54] DAN: The fact that animals eat a particular mushroom doesn't mean that it is safe for humans, either. [Q13-14]\n[03:02] ANNOUNCER: Transition to Questions 15–20.\n[04:01] DAN: If you don't have a car, you can still start looking for mushrooms in local parks. Areas with grass and trees can be good places to begin.\n[04:22] DAN: It's best to go early in the morning. Other people or animals may have collected the mushrooms if you arrive later in the day. [Q15]\n[04:38] DAN: If you're a beginner, don't go into the forest alone. You shouldn't depend entirely on pictures in a book, either. Mobile applications can help you identify different species, but remember that the phone signal may be unreliable in wooded areas.\n[04:59] DAN: A much better option is to join a group led by someone with experience. That way, you can learn how to identify mushrooms safely. [Q16]\n[05:08] DAN: It's also important to protect the natural environment. Don't collect every mushroom you find. Only take what you actually need, and be careful not to damage young mushrooms or nearby plants. [Q17]\n[05:30] DAN: You must also avoid collecting species that are protected or at risk of disappearing.\n[05:38] DAN: Unfortunately, some wild mushroom populations are declining. This is partly because restaurants want to serve more locally collected food. In addition, building developments and the removal of water from natural areas can damage mushroom habitats. [Q18]\n[06:09] DAN: Once you've collected mushrooms, put them in a brown paper bag and keep them in the refrigerator. You should use them within a few days and wash them carefully before preparing them. [Q19]\n[06:30] DAN: There are plenty of ways to cook wild mushrooms. You can add them to stir-fries, risotto or pasta dishes. However, when trying a new type, it's sensible to start with a small quantity because some people may have an unexpected reaction. [Q20]\n[07:01] PRESENTER: Before we finish, could you give us a few examples of poisonous mushrooms that people should be particularly careful about?\n[07:11] ANNOUNCER: End of Part 2."
            ),
            ListeningSectionDto(
                sectionNumber = 3,
                title = "Part 3: Automation and the Future of Work",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758712/%E5%89%9118_section3_part1.mp3",
                durationSeconds = 330,
                transcript = "[00:00] ANNOUNCER: Introduction. Two business students are discussing a seminar they attended about automation, technological development and how these changes may affect employment in the future.\n[00:41] STUDENT 1: That seminar we attended yesterday about automation and the future of work was really interesting, wasn't it?\n[00:48] STUDENT 1: I particularly enjoyed learning about the First Industrial Revolution in Britain during the nineteenth century and how people reacted to the changes taking place at that time.\n[00:57] STUDENT 2: Yes, it was fascinating to find out how workers felt about the new technology and the difficulties they experienced.\n[01:03] STUDENT 2: At first, many textile workers strongly opposed the introduction of new machines.\n[01:08] STUDENT 1: That's right. The Luddites, for example, actually destroyed weaving machines because they were afraid that these machines would take away their jobs.\n[01:16] STUDENT 2: Their protests weren't successful in stopping industrialisation. In the long run, the development of industry created more employment opportunities than the number of jobs it replaced. [Q21-22]\n[01:24] STUDENT 1: I agree that things improved over time, but I can understand why those workers felt threatened. They had good reasons to worry about their future. [Q21-22]\n[01:32] STUDENT 2: The seminar seemed quite optimistic about modern technology. Robots could take over boring and repetitive tasks, allowing people to concentrate on more interesting work.\n[01:51] STUDENT 1: I'm not completely convinced. For instance, people often predict that technology will give us more free time, but the evidence seems to suggest that working hours are actually increasing. [Q23-24]\n[02:02] STUDENT 2: I also doubt that unemployment will decrease in the near future. Perhaps the situation will improve over a longer period. [Q23-24]\n[02:12] STUDENT 2: Another issue is that people may have to continue working until an older age because life expectancy is increasing.\n[02:19] STUDENT 1: That's already happening in some places, isn't it?\n[02:23] ANNOUNCER: Transition to Questions 25–30.\n[03:07] STUDENT 1: I find it particularly interesting to look at how technology has affected different professions.\n[03:13] STUDENT 2: Take accountants, for example. Although computers have changed the way they work, the number of people employed in accounting has actually increased. Technology allows them to complete their tasks more efficiently, but accountants are still needed. [Q25]\n[03:28] STUDENT 1: That's surprising. I wouldn't have expected the number of accountants to be around twenty times higher than it was in the nineteenth century.\n[03:35] STUDENT 2: Hairdressers are another interesting example. There are now approximately one hairdresser for every 287 people, compared with more than 1,500 people for each hairdresser in the past.\n[03:48] STUDENT 1: That may be because people have higher incomes and can afford to spend more money on personal services. Technology hasn't fundamentally changed the nature of hairdressing. [Q26]\n[04:04] STUDENT 2: Administrative work has changed much more dramatically. In the past, secretaries mainly took dictation and typed documents.\n[04:14] STUDENT 1: Nowadays, administrative employees often have greater responsibilities and a higher professional status. These jobs can even attract people with university degrees. [Q27]\n[04:21] STUDENT 1: Agriculture is another example. According to the 1871 census, approximately a quarter of the population worked on farms.\n[04:37] STUDENT 2: That's a huge difference from today, when the proportion is less than 0.2 per cent. [Q28]\n[04:43] STUDENT 1: Care work is also worth considering. In the past, fewer people needed long-term care because life expectancy was shorter. As people live longer, demand for care workers is expected to increase. [Q29]\n[04:54] STUDENT 2: In fact, there are already shortages of workers in the care sector.\n[04:58] STUDENT 1: What about bank tellers? Their numbers have fallen since automated teller machines were introduced in the 1980s.\n[05:05] STUDENT 2: And technology may eventually make many traditional banking tasks unnecessary. It's difficult to predict exactly what will happen to those jobs in the future. [Q30]\n[05:19] ANNOUNCER: End of Part 3."
            ),
            ListeningSectionDto(
                sectionNumber = 4,
                title = "Part 4: Space Traffic Management",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758712/%E5%89%9118_section3_part1.mp3",
                durationSeconds = 390,
                transcript = "[00:00] ANNOUNCER: Introduction. A university lecturer gives a talk about the increasing number of objects in Earth's orbit and the need to manage their movement safely.\n[01:16] LECTURER: In today's lecture, I'm going to discuss the management of objects moving around the Earth, particularly artificial satellites. This area is commonly known as space traffic management.\n[01:30] LECTURER: You can think of it as being similar to the international air traffic control system, which helps aircraft travel safely through the air.\n[01:43] LECTURER: The main purpose of space traffic management is to reduce the possibility of collisions between objects orbiting our planet.\n[01:57] LECTURER: To achieve this, we need both legal regulations and suitable technical systems. These would help prevent accidents and make space activities safer. [Q31]\n[02:12] LECTURER: At present, there is no fully effective global system for managing space traffic. One important reason is that launching satellites has become much less expensive, allowing more organisations to send objects into space. [Q32]\n[02:33] LECTURER: The number of satellites is growing rapidly. Some organisations are now launching large groups, or constellations, of thousands of satellites that operate together rather than relying on individual satellites. [Q33]\n[02:55] LECTURER: One difficulty is that satellites do not always send information identifying themselves back to Earth. [Q34]\n[03:09] LECTURER: This is different from aviation, where aircraft are monitored through an international system. Current satellite-tracking methods are not yet sufficient to guarantee safety and prevent every possible collision. [Q35]\n[03:30] LECTURER: Another serious issue is space debris. This includes fragments from damaged satellites and waste produced by space stations. Even very small pieces can cause significant damage, and some are extremely difficult to detect.\n[03:52] LECTURER: Some satellite operators are unwilling to share information about their spacecraft. They may have military or commercial reasons for keeping that information private. [Q36]\n[04:11] LECTURER: Even when information is available, gathering and processing it can be difficult. We need to know the characteristics of each object and where it is located at any particular moment. [Q37]\n[04:26] LECTURER: Satellites can be very small and travel at speeds of thousands of kilometres per hour. This makes it extremely challenging to track every object continuously.\n[04:42] LECTURER: Since it isn't possible to monitor everything all the time, scientists have to calculate and predict the future paths of objects in orbit. [Q38]\n[04:54] ANNOUNCER: Transition to possible solutions.\n[05:01] LECTURER: One possible solution is to make relevant information accessible around the world. Countries and satellite operators would need to agree on common standards for recording and presenting data.\n[05:22] LECTURER: Establishing these standards will not be easy, but it should be possible. Other scientific fields have already managed to develop systems for sharing information internationally.\n[05:36] LECTURER: Another proposal is to create a shared database containing information about objects in space. [Q39]\n[05:43] LECTURER: The situation is becoming more urgent because Earth's orbit will become increasingly crowded. Better coordination between spacecraft operators is therefore essential.\n[06:04] LECTURER: Finally, cooperation depends on trust. This needs to exist not only between individual organisations but also at national and international levels. [Q40]\n[06:22] ANNOUNCER: End of Part 4."
            )
        )

        val questions = listOf(
            ListeningQuestionDetailDto(
                id = "cam18_t3_q1",
                position = 1,
                type = "FILL_IN_BLANK",
                prompt = "Wayside Camera Club membership form\nComplete the form below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nName: Dan Green\nEmail address: dan1068@market.com\nHome address: 52 1 ________ Street, Peacetown\nHeard about us: from a [ 2 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 124,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q2",
                position = 2,
                type = "FILL_IN_BLANK",
                prompt = "Wayside Camera Club membership form\nComplete the form below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nName: Dan Green\nHome address: 52 [ 1 ] Street, Peacetown\nHeard about us: from a 2 ________\nReasons for joining:\nâ€¢ to enter competitions\nâ€¢ to [ 3 ] with other photographers",
                options = null,
                sectionNumber = 1,
                timestampStart = 146,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q3",
                position = 3,
                type = "FILL_IN_BLANK",
                prompt = "Wayside Camera Club membership form\nComplete the form below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nHeard about us: from a [ 2 ]\nReasons for joining:\nâ€¢ to enter competitions\nâ€¢ to 3 ________ with other photographers\nType of membership: [ 4 ] membership (Â£30)",
                options = null,
                sectionNumber = 1,
                timestampStart = 178,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q4",
                position = 4,
                type = "FILL_IN_BLANK",
                prompt = "Wayside Camera Club membership form\nComplete the form below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nReasons for joining:\nâ€¢ to enter competitions\nâ€¢ to [ 3 ] with other photographers\nType of membership: 4 ________ membership (Â£30)",
                options = null,
                sectionNumber = 1,
                timestampStart = 195,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q5",
                position = 5,
                type = "FILL_IN_BLANK",
                prompt = "Dan's photography\nComplete the table below. Write NO MORE THAN TWO WORDS for each answer.\n\nTitle of competition: 5 ________\nInstructions: A scene in the home\nFeedback to Dan: The picture's composition was not good",
                options = null,
                sectionNumber = 1,
                timestampStart = 266,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q6",
                position = 6,
                type = "FILL_IN_BLANK",
                prompt = "Dan's photography\nComplete the table below. Write NO MORE THAN TWO WORDS for each answer.\n\nTitle of competition: 'Beautiful Sunsets'\nInstructions: Scene must show some 6 ________\nFeedback to Dan: The [ 7 ] was wrong",
                options = null,
                sectionNumber = 1,
                timestampStart = 323,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q7",
                position = 7,
                type = "FILL_IN_BLANK",
                prompt = "Dan's photography\nComplete the table below. Write NO MORE THAN TWO WORDS for each answer.\n\nTitle of competition: 'Beautiful Sunsets'\nInstructions: Scene must show some [ 6 ]\nFeedback to Dan: The 7 ________ was wrong",
                options = null,
                sectionNumber = 1,
                timestampStart = 340,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q8",
                position = 8,
                type = "FILL_IN_BLANK",
                prompt = "Dan's photography\nComplete the table below. Write NO MORE THAN TWO WORDS for each answer.\n\nTitle of competition: 8 ________\nInstructions: Scene must show [ 9 ]\nFeedback to Dan: The photograph was too [ 10 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 355,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q9",
                position = 9,
                type = "FILL_IN_BLANK",
                prompt = "Dan's photography\nComplete the table below. Write NO MORE THAN TWO WORDS for each answer.\n\nTitle of competition: [ 8 ]\nInstructions: Scene must show 9 ________\nFeedback to Dan: The photograph was too [ 10 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 359,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q10",
                position = 10,
                type = "FILL_IN_BLANK",
                prompt = "Dan's photography\nComplete the table below. Write NO MORE THAN TWO WORDS for each answer.\n\nTitle of competition: [ 8 ]\nInstructions: Scene must show [ 9 ]\nFeedback to Dan: The photograph was too 10 ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 375,
                timestampEnd = 0
            ),
            // Section 2: Q11-20
            ListeningQuestionDetailDto(
                id = "cam18_t3_q11",
                position = 11,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 11 and 12: Choose TWO letters, A–E.\nWhich TWO warnings does Dan give about picking mushrooms?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf(
                    "A. Don't pick more than one variety of mushroom at a time.",
                    "B. Don't pick mushrooms near busy roads.",
                    "C. Don't eat mushrooms given to you.",
                    "D. Don't eat mushrooms while picking them.",
                    "E. Don't pick old mushrooms."
                ),
                sectionNumber = 2,
                timestampStart = 82,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q12",
                position = 12,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 11 and 12: Choose TWO letters, A–E.\nWhich TWO warnings does Dan give about picking mushrooms?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf(
                    "A. Don't pick more than one variety of mushroom at a time.",
                    "B. Don't pick mushrooms near busy roads.",
                    "C. Don't eat mushrooms given to you.",
                    "D. Don't eat mushrooms while picking them.",
                    "E. Don't pick old mushrooms."
                ),
                sectionNumber = 2,
                timestampStart = 82,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q13",
                position = 13,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 13 and 14: Choose TWO letters, A–E.\nWhich TWO ideas about wild mushrooms does Dan say are correct?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf(
                    "A. Mushrooms should always be peeled before eating.",
                    "B. Mushrooms eaten by animals may be unsafe.",
                    "C. Cooking destroys toxins in mushrooms.",
                    "D. Brightly coloured mushrooms can be edible.",
                    "E. All poisonous mushrooms have a bad smell."
                ),
                sectionNumber = 2,
                timestampStart = 174,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q14",
                position = 14,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 13 and 14: Choose TWO letters, A–E.\nWhich TWO ideas about wild mushrooms does Dan say are correct?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf(
                    "A. Mushrooms should always be peeled before eating.",
                    "B. Mushrooms eaten by animals may be unsafe.",
                    "C. Cooking destroys toxins in mushrooms.",
                    "D. Brightly coloured mushrooms can be edible.",
                    "E. All poisonous mushrooms have a bad smell."
                ),
                sectionNumber = 2,
                timestampStart = 128,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q15",
                position = 15,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 15–20: Choose the correct letter, A, B or C.\n\n15. What advice does Dan give about picking mushrooms in parks?",
                options = listOf(
                    "A. Choose wooded areas.",
                    "B. Don't disturb wildlife.",
                    "C. Get there early."
                ),
                sectionNumber = 2,
                timestampStart = 262,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q16",
                position = 16,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 15–20: Choose the correct letter, A, B or C.\n\n16. Dan says it is a good idea for beginners to",
                options = listOf(
                    "A. use a mushroom app.",
                    "B. join a group.",
                    "C. take a reference book."
                ),
                sectionNumber = 2,
                timestampStart = 299,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q17",
                position = 17,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 15–20: Choose the correct letter, A, B or C.\n\n17. What does Dan say is important for conservation?",
                options = listOf(
                    "A. selecting only fully grown mushrooms.",
                    "B. picking a limited amount of mushrooms.",
                    "C. avoiding areas where rare mushroom species grow."
                ),
                sectionNumber = 2,
                timestampStart = 308,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q18",
                position = 18,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 15–20: Choose the correct letter, A, B or C.\n\n18. According to Dan, some varieties of wild mushrooms are in decline because there is",
                options = listOf(
                    "A. a huge demand for them from restaurants.",
                    "B. a lack of rain in this part of the country.",
                    "C. a rise in building developments locally."
                ),
                sectionNumber = 2,
                timestampStart = 338,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q19",
                position = 19,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 15–20: Choose the correct letter, A, B or C.\n\n19. Dan says that when storing mushrooms, people should",
                options = listOf(
                    "A. keep them in the fridge for no more than two days.",
                    "B. keep them in a brown bag in a dark room.",
                    "C. leave them for a period after washing them."
                ),
                sectionNumber = 2,
                timestampStart = 369,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q20",
                position = 20,
                type = "MULTIPLE_CHOICE",
                prompt = "Picking wild mushrooms\nQuestions 15–20: Choose the correct letter, A, B or C.\n\n20. What does Dan say about trying new varieties of mushrooms?",
                options = listOf(
                    "A. Experiment with different recipes.",
                    "B. Expect some to have a strong taste.",
                    "C. Cook them for a long time."
                ),
                sectionNumber = 2,
                timestampStart = 390,
                timestampEnd = 0
            ),
            // Section 3: Q21-30
            ListeningQuestionDetailDto(
                id = "cam18_t3_q21",
                position = 21,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 21 and 22: Choose TWO letters, A–E.\nWhich TWO opinions about the Luddites do the students express?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf(
                    "A. Their actions were ineffective.",
                    "B. They are still influential today.",
                    "C. They have received unfair criticism.",
                    "D. They were proved right.",
                    "E. Their attitude is understandable."
                ),
                sectionNumber = 3,
                timestampStart = 76,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q22",
                position = 22,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 21 and 22: Choose TWO letters, A–E.\nWhich TWO opinions about the Luddites do the students express?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf(
                    "A. Their actions were ineffective.",
                    "B. They are still influential today.",
                    "C. They have received unfair criticism.",
                    "D. They were proved right.",
                    "E. Their attitude is understandable."
                ),
                sectionNumber = 3,
                timestampStart = 84,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q23",
                position = 23,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 23 and 24: Choose TWO letters, A–E.\nWhich TWO predictions about the future of work are the students doubtful about?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf(
                    "A. Work will be more rewarding.",
                    "B. Unemployment will fall.",
                    "C. People will want to delay retiring.",
                    "D. Working hours will be shorter.",
                    "E. People will change jobs more frequently."
                ),
                sectionNumber = 3,
                timestampStart = 122,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q24",
                position = 24,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 23 and 24: Choose TWO letters, A–E.\nWhich TWO predictions about the future of work are the students doubtful about?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf(
                    "A. Work will be more rewarding.",
                    "B. Unemployment will fall.",
                    "C. People will want to delay retiring.",
                    "D. Working hours will be shorter.",
                    "E. People will change jobs more frequently."
                ),
                sectionNumber = 3,
                timestampStart = 111,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q25",
                position = 25,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 25–30: What comment do the students make about each of the following jobs?\nChoose SIX answers from the box and write the correct letter, A–G, next to Questions 25–30.\n\nComments:\nA. These jobs are likely to be at risk\nB. Their role has become more interesting in recent years\nC. The number of people working in this sector has fallen dramatically\nD. This job will require more qualifications\nE. Higher disposable income has led to a huge increase in jobs\nF. There is likely to be a significant rise in demand for this service\nG. Both employment and productivity have risen\n\n25. Accountants",
                options = listOf(
                    "A. These jobs are likely to be at risk",
                    "B. Their role has become more interesting in recent years",
                    "C. The number of people working in this sector has fallen dramatically",
                    "D. This job will require more qualifications",
                    "E. Higher disposable income has led to a huge increase in jobs",
                    "F. There is likely to be a significant rise in demand for this service",
                    "G. Both employment and productivity have risen"
                ),
                sectionNumber = 3,
                timestampStart = 193,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q26",
                position = 26,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 25–30: What comment do the students make about each of the following jobs?\nChoose SIX answers from the box and write the correct letter, A–G, next to Questions 25–30.\n\nComments:\nA. These jobs are likely to be at risk\nB. Their role has become more interesting in recent years\nC. The number of people working in this sector has fallen dramatically\nD. This job will require more qualifications\nE. Higher disposable income has led to a huge increase in jobs\nF. There is likely to be a significant rise in demand for this service\nG. Both employment and productivity have risen\n\n26. Hairdressers",
                options = listOf(
                    "A. These jobs are likely to be at risk",
                    "B. Their role has become more interesting in recent years",
                    "C. The number of people working in this sector has fallen dramatically",
                    "D. This job will require more qualifications",
                    "E. Higher disposable income has led to a huge increase in jobs",
                    "F. There is likely to be a significant rise in demand for this service",
                    "G. Both employment and productivity have risen"
                ),
                sectionNumber = 3,
                timestampStart = 228,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q27",
                position = 27,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 25–30: What comment do the students make about each of the following jobs?\nChoose SIX answers from the box and write the correct letter, A–G, next to Questions 25–30.\n\nComments:\nA. These jobs are likely to be at risk\nB. Their role has become more interesting in recent years\nC. The number of people working in this sector has fallen dramatically\nD. This job will require more qualifications\nE. Higher disposable income has led to a huge increase in jobs\nF. There is likely to be a significant rise in demand for this service\nG. Both employment and productivity have risen\n\n27. Administrative staff",
                options = listOf(
                    "A. These jobs are likely to be at risk",
                    "B. Their role has become more interesting in recent years",
                    "C. The number of people working in this sector has fallen dramatically",
                    "D. This job will require more qualifications",
                    "E. Higher disposable income has led to a huge increase in jobs",
                    "F. There is likely to be a significant rise in demand for this service",
                    "G. Both employment and productivity have risen"
                ),
                sectionNumber = 3,
                timestampStart = 254,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q28",
                position = 28,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 25–30: What comment do the students make about each of the following jobs?\nChoose SIX answers from the box and write the correct letter, A–G, next to Questions 25–30.\n\nComments:\nA. These jobs are likely to be at risk\nB. Their role has become more interesting in recent years\nC. The number of people working in this sector has fallen dramatically\nD. This job will require more qualifications\nE. Higher disposable income has led to a huge increase in jobs\nF. There is likely to be a significant rise in demand for this service\nG. Both employment and productivity have risen\n\n28. Agricultural workers",
                options = listOf(
                    "A. These jobs are likely to be at risk",
                    "B. Their role has become more interesting in recent years",
                    "C. The number of people working in this sector has fallen dramatically",
                    "D. This job will require more qualifications",
                    "E. Higher disposable income has led to a huge increase in jobs",
                    "F. There is likely to be a significant rise in demand for this service",
                    "G. Both employment and productivity have risen"
                ),
                sectionNumber = 3,
                timestampStart = 277,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q29",
                position = 29,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 25–30: What comment do the students make about each of the following jobs?\nChoose SIX answers from the box and write the correct letter, A–G, next to Questions 25–30.\n\nComments:\nA. These jobs are likely to be at risk\nB. Their role has become more interesting in recent years\nC. The number of people working in this sector has fallen dramatically\nD. This job will require more qualifications\nE. Higher disposable income has led to a huge increase in jobs\nF. There is likely to be a significant rise in demand for this service\nG. Both employment and productivity have risen\n\n29. Care workers",
                options = listOf(
                    "A. These jobs are likely to be at risk",
                    "B. Their role has become more interesting in recent years",
                    "C. The number of people working in this sector has fallen dramatically",
                    "D. This job will require more qualifications",
                    "E. Higher disposable income has led to a huge increase in jobs",
                    "F. There is likely to be a significant rise in demand for this service",
                    "G. Both employment and productivity have risen"
                ),
                sectionNumber = 3,
                timestampStart = 283,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q30",
                position = 30,
                type = "MULTIPLE_CHOICE",
                prompt = "Automation and the future of work\nQuestions 25–30: What comment do the students make about each of the following jobs?\nChoose SIX answers from the box and write the correct letter, A–G, next to Questions 25–30.\n\nComments:\nA. These jobs are likely to be at risk\nB. Their role has become more interesting in recent years\nC. The number of people working in this sector has fallen dramatically\nD. This job will require more qualifications\nE. Higher disposable income has led to a huge increase in jobs\nF. There is likely to be a significant rise in demand for this service\nG. Both employment and productivity have risen\n\n30. Bank clerks",
                options = listOf(
                    "A. These jobs are likely to be at risk",
                    "B. Their role has become more interesting in recent years",
                    "C. The number of people working in this sector has fallen dramatically",
                    "D. This job will require more qualifications",
                    "E. Higher disposable income has led to a huge increase in jobs",
                    "F. There is likely to be a significant rise in demand for this service",
                    "G. Both employment and productivity have risen"
                ),
                sectionNumber = 3,
                timestampStart = 305,
                timestampEnd = 0
            ),
            // Section 4: Q31-40
            ListeningQuestionDetailDto(
                id = "cam18_t3_q31",
                position = 31,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nA Space Traffic Management system\n• is a concept similar to Air Traffic Control, but for satellites\n• would aim to set up legal and 31 ________ ways of improving safety\n• does not currently exist",
                options = null,
                sectionNumber = 4,
                timestampStart = 117,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q32",
                position = 32,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nProblems in developing effective Space Traffic Management\n• Satellites are now quite 32 ________ and therefore more widespread (e.g. there are constellations made up of [ 33 ] of satellites).",
                options = null,
                sectionNumber = 4,
                timestampStart = 132,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q33",
                position = 33,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nProblems in developing effective Space Traffic Management\n• Satellites are now quite [ 32 ] and therefore more widespread (e.g. there are constellations made up of 33 ________ of satellites).",
                options = null,
                sectionNumber = 4,
                timestampStart = 153,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q34",
                position = 34,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nProblems in developing effective Space Traffic Management\n• At present, satellites are not required to transmit information to help with their 34 ________.\n• There are few systems for [ 35 ] satellites.",
                options = null,
                sectionNumber = 4,
                timestampStart = 175,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q35",
                position = 35,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nProblems in developing effective Space Traffic Management\n• At present, satellites are not required to transmit information to help with their [ 34 ].\n• There are few systems for 35 ________ satellites.\n• Small pieces of debris may be difficult to identify.",
                options = null,
                sectionNumber = 4,
                timestampStart = 189,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q36",
                position = 36,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nProblems in developing effective Space Traffic Management\n• Operators may be unwilling to share details of satellites used for 36 ________ or commercial reasons.\n• It may be hard to collect details of the object's [ 37 ] at a given time.",
                options = null,
                sectionNumber = 4,
                timestampStart = 232,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q37",
                position = 37,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nProblems in developing effective Space Traffic Management\n• Operators may be unwilling to share details of satellites used for [ 36 ] or commercial reasons.\n• It may be hard to collect details of the object's 37 ________ at a given time.\n• Scientists can only make a [ 38 ] about where the satellite will go.",
                options = null,
                sectionNumber = 4,
                timestampStart = 251,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q38",
                position = 38,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nProblems in developing effective Space Traffic Management\n• It may be hard to collect details of the object's [ 37 ] at a given time.\n• Scientists can only make a 38 ________ about where the satellite will go.",
                options = null,
                sectionNumber = 4,
                timestampStart = 282,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q39",
                position = 39,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nSolutions\n• Common standards should be agreed on for the presentation of information.\n• The information should be combined in one 39 ________.\n• A coordinated system must be designed to create [ 40 ] in its users.",
                options = null,
                sectionNumber = 4,
                timestampStart = 336,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t3_q40",
                position = 40,
                type = "FILL_IN_BLANK",
                prompt = "Space Traffic Management\nSolutions\n• The information should be combined in one [ 39 ].\n• A coordinated system must be designed to create 40 ________ in its users.",
                options = null,
                sectionNumber = 4,
                timestampStart = 364,
                timestampEnd = 0
            )
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


    private fun getCam18Test4Detail(testItem: com.example.ielts.data.remote.dto.ListeningTestListItemDto): ListeningTestDetailDto {
        val sections = listOf(
            ListeningSectionDto(
                sectionNumber = 1,
                title = "Part 1: Medical Centre Receptionist Job",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758844/%E5%89%9118_section4_part1.mp3",
                durationSeconds = 395,
                transcript = "[00:30] ANNOUNCER: Part 1. You will hear a man from an employment agency talking to a woman about a job vacancy.\n[01:22] JULIE: Hello?\n[01:23] GREG: Oh, hello. Is this Julie Davison?\n[01:26] JULIE: Yes, speaking.\n[01:28] GREG: Yes. This is Greg Preston.\n[01:35] GREG: From the Employment Agency. We met last week when you came in to enquire about office work.\n[01:39] JULIE: Oh, yes, I remember.\n[01:41] GREG: We've just received information about a vacancy that might suit you.\n[01:44] JULIE: OK. What kind of position is it?\n[01:46] GREG: It's a receptionist job. I believe you've done similar work before, haven't you? [Q1]\n[01:57] JULIE: Yes. I worked at a sports centre for a couple of years before getting married and having children.\n[02:02] GREG: The job is in Fordham, so it shouldn't be too far for you to travel. You'll be working at the local medical centre. [Q2]\n[02:07] JULIE: All right. Where exactly is the centre?\n[02:14] GREG: It's near the railway station, on Chastons Road. [Q3]\n[02:20] JULIE: Sorry, could you repeat the name?\n[02:24] GREG: Chastons Road. That's spelled C-H-A-S-T-O-N-S.\n[02:31] JULIE: Thanks. What would my main responsibilities be? Would I answer patients' questions?\n[02:38] GREG: Yes. You would also arrange appointments, either in person or over the telephone, and change appointment times when necessary. [Q4]\n[02:46] JULIE: That sounds manageable.\n[02:49] GREG: Another responsibility would be updating the medical centre's database. [Q5] There would also be some general administrative tasks, but those are the main duties.\n[03:20] ANNOUNCER: Before you hear the rest of the conversation, you have some time to look at questions 6 to 10.\n[03:58] GREG: When I received the job details, I immediately thought of you. The employer specifically needs somebody with previous experience, [Q6] and I remembered your work at the sports centre.\n[04:08] JULIE: I enjoyed that job. Are there any other qualities they want?\n[04:18] GREG: The medical centre can be a stressful workplace. It's usually very busy, and patients may be worried or anxious. They need somebody who can remain calm and communicate confidently with members of the public. [Q7]\n[04:38] JULIE: After looking after three children under five, I think I can deal with stressful situations!\n[04:45] GREG: I'm sure you can. They also want someone with good computer skills.\n[04:52] JULIE: That won't be a problem.\n[04:58] JULIE: When would the job begin?\n[05:03] GREG: They're hoping the successful applicant can start at the beginning of next month. However, it's a temporary position rather than a permanent one. [Q8] The contract will run until the end of September, although there may be other opportunities later.\n[05:17] JULIE: That's fine. What are the working hours?\n[05:23] GREG: You would need to start at 7:45 in the morning. Would that be possible?\n[05:27] JULIE: Yes. My husband would have to get the children ready and take them to my mother's house, where she'll look after them while I'm working.\n[05:34] JULIE: What time would I finish?\n[05:37] GREG: At 1:15 p.m. [Q9]\n[05:40] JULIE: That should be convenient. I could collect the children on my way home and spend the afternoon with them.\n[05:48] JULIE: Is there a car park for employees at the medical centre? [Q10]\n[05:51] GREG: Yes, staff can park there. The centre is also served by a bus route.\n[05:55] JULIE: I'll probably drive, but it's useful to know there's another option. What should I do next?\n[06:02] GREG: If you agree, I'll send your CV and references to the employer. After that, you should contact them directly to arrange an interview.\n[06:12] JULIE: Excellent. Thanks very much for your help.\n[06:17] GREG: You're welcome. Goodbye.\n[06:19] JULIE: Bye.\n[06:22] ANNOUNCER: End of Part 1."
            ),
            ListeningSectionDto(
                sectionNumber = 2,
                title = "Part 2: Museum of Farming Life",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758844/%E5%89%9118_section4_part1.mp3",
                durationSeconds = 410,
                transcript = "[00:50] GUIDE: Good morning, everyone, and welcome to the Museum of Farming Life. Since this is your first visit, I'll explain the museum's history and introduce the different things you can see here.\n[01:07] GUIDE: We're standing at the entrance to a large building originally constructed in 1880. It was the private home of Alfred Palmer, a local businessman associated with the Palmer Biscuit Factory. [Q11]\n[01:21] GUIDE: The building later became student accommodation in 1911, before being converted into a museum in 1951. A modern extension was added in 2005 to provide more space for the collections.\n[01:41] GUIDE: The university owns the museum building. Apart from two rooms used as our offices, the university uses the main part of the building for teaching. [Q12]\n[01:54] GUIDE: You may see university students attending lessons there, but visitors cannot enter that section. It's unfortunate because the building has some remarkable architectural features, especially the former library.\n[02:09] GUIDE: Fortunately, admission to the museum is free. This includes the galleries, the outdoor spaces and the rooms used for special exhibitions.\n[02:24] GUIDE: We also organise free activities for children and students, including a museum club. We do have a donation box, though, if you'd like to contribute. [Q13]\n[02:42] GUIDE: There's a cloakroom where you can leave coats and bags. Unlike many museums, we're happy for visitors to take photographs, so you may want to keep your cameras with you.\n[02:56] GUIDE: However, carrying a heavy backpack around may be uncomfortable. Please keep your coats on because the museum garden is quite cold today. [Q14]\n[03:51] GUIDE: Let me introduce the different areas of the museum, starting with Four Seasons, which is situated near the main gallery.\n[04:06] GUIDE: Four Seasons features a short animated film showing a woodland environment. Young people studying film production created it especially for this museum. Both children and adults tend to enjoy it. [Q15]\n[04:20] GUIDE: The main gallery is called Town and Country. It contains a photographic collection featuring prize-winning sheep and the people who look after them.\n[04:35] GUIDE: Next is Farmhouse Kitchen. As the name suggests, this area focuses on traditional farm kitchens. It houses the country's oldest collection of equipment used to make butter and cheese.\n[04:50] GUIDE: A specialist cheesemaker will demonstrate the production process this morning, and visitors may even have the chance to taste the finished product. [Q16]\n[04:58] GUIDE: After that, you can go upstairs to a landing with a view over the galleries, or turn left into a room called A Year on the Farm.\n[05:05] GUIDE: That room has plenty of seats. We sometimes use it for school groups, so it's a good place to take a break. Visitors can also test their memory by answering questions about the exhibits they've seen. [Q17]\n[05:27] GUIDE: Another section is Waggon Walk, which displays farm carts collected from different parts of the country. The designs vary considerably from region to region, and information boards explain the background of each cart.\n[05:50] GUIDE: These carts are old and fragile, so please keep children close and don't allow them to climb on the displays. [Q18]\n[05:58] GUIDE: From Waggon Walk, you can return to reception, visit the garden or go back to the galleries.\n[06:06] GUIDE: One garden area, called Bees are Magic, is currently being redeveloped and cannot be visited. However, you can still purchase our honey in the museum shop. [Q19]\n[06:19] GUIDE: Finally, the pond is home to a variety of wildlife, including very young ducklings and small frogs.\n[06:34] GUIDE: There's a fence around the pond, and the water isn't deep, so the area is safe for children. [Q20]"
            ),
            ListeningSectionDto(
                sectionNumber = 3,
                title = "Part 3: Origami in Education",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758844/%E5%89%9118_section4_part1.mp3",
                durationSeconds = 390,
                transcript = "[01:01] TUTOR: I'd like you to discuss the lesson we watched on video and consider how origami, the Japanese art of paper folding, can be used as an educational activity. Please work with the person sitting next to you.\n[01:16] STUDENT 1: I hadn't realised that folding a simple square of paper into something like a bird could be such an effective learning tool. It has completely changed the way I think about origami.\n[01:28] STUDENT 2: I agree. The children were developing several educational skills. We could see how carefully they listened to the instructions and followed the steps in the correct order. [Q21-22]\n[01:45] STUDENT 1: They were working independently in this lesson. It would also be interesting to let them try making something without detailed instructions. That could encourage them to cooperate and develop teamwork.\n[02:03] STUDENT 2: That would be more challenging, but one thing I particularly liked was that the children were enjoying themselves while learning something new. [Q21-22]\n[02:15] STUDENT 1: The children in the video managed the folding well. For younger pupils, though, origami could help develop fine motor skills.\n[02:22] STUDENT 2: Shall we discuss the individual children we observed? I wrote down their names and made some notes.\n[02:29] STUDENT 1: Good idea. Let's begin with Sid.\n[02:37] STUDENT 2: Before the activity, Sid was rather disruptive. Once he started folding the paper, however, he became calmer and more focused. [Q23]\n[02:51] STUDENT 1: What about Jack? He seemed keen to solve problems by himself. Instead of immediately asking the teacher for help, he experimented with different approaches. [Q24]\n[03:06] STUDENT 2: Naomi appeared to lose interest for a while. Then she became motivated because she wanted to make the best paper mouse. Even so, she didn't seem entirely happy with the result. [Q25]\n[03:18] STUDENT 1: Anya did extremely well. She listened closely and produced a very good bird without much difficulty. The teacher could probably have given her a more challenging task. [Q26]\n[03:34] STUDENT 2: Zara seemed unfamiliar with origami and initially didn't understand what she needed to do. Her final result was reasonable, though, and the activity probably gave her a positive learning experience. [Q27]\n[04:39] STUDENT 1: I think the activity worked well partly because the teacher had prepared the lesson carefully.\n[04:46] STUDENT 2: Yes. Preparing examples for every stage of the folding process must have taken time, but it was an effective idea. The children could see the final goal and handle physical examples instead of relying only on pictures. [Q28]\n[05:04] STUDENT 1: Those examples also helped the children understand the teacher's spoken instructions.\n[05:11] STUDENT 2: It's surprising that origami isn't used more often. Perhaps teachers think children who spend a lot of time using computers won't find it interesting, particularly boys.\n[05:23] STUDENT 1: I'm not sure that's the main reason. Origami is similar to other craft activities. Maybe some teachers simply don't feel confident doing practical work with their hands. [Q29]\n[05:36] STUDENT 2: We should try it in our Year 3 maths teaching practice. It could help children remember geometric shapes, as they did in the video.\n[05:50] STUDENT 1: It might also be useful when we teach fractions, which is coming up soon. [Q30]\n[06:02] STUDENT 2: And origami can help children practise symmetry, although I think they covered that last term.\n[06:13] STUDENT 1: Let's gather some ideas and plan the lesson next week."
            ),
            ListeningSectionDto(
                sectionNumber = 4,
                title = "Part 4: Victor Hugo",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758844/%E5%89%9118_section4_part1.mp3",
                durationSeconds = 440,
                transcript = "[01:06] LECTURER: The person I've chosen to discuss is Victor Hugo, a famous French writer of the nineteenth century.\n" +
                    "[01:18] LECTURER: Many people know him because of his novel Les Misérables, published in 1862. The story became a famous stage musical in the 1980s, and a film adaptation was released in 2012.\n" +
                    "[01:41] LECTURER: Although people may know the general plot, they often know less about the author himself. [Q31] Today, I'll focus on his home on the island of Guernsey, one of the British Channel Islands.\n" +
                    "[02:03] LECTURER: Hugo was highly respected in Paris and was a leading figure in the Romantic literary movement. Besides writing, he spoke publicly about social problems, including poverty and the need for educational reform. [Q32]\n" +
                    "[02:31] LECTURER: His criticism of the authorities was unwelcome. Eventually, Napoleon III ordered him to leave Paris and prohibited him from returning. Hugo was therefore forced into exile.\n" +
                    "[02:56] LECTURER: He lived in several places elsewhere in Europe. [Q33] Guernsey was his third place of exile, and he arrived there in 1855. During his stay, he continued writing, including work on Les Misérables.\n" +
                    "[03:14] LECTURER: He bought the property using money earned from publishing a collection of poetry in France. [Q34] It was the only property he ever owned, and he took great pride in it.\n" +
                    "[03:38] LECTURER: The house was a large, five-storey building in St Peter Port, the island's capital. Hugo lived there for fifteen years and returned to France in 1870 after Napoleon's empire fell.\n" +
                    "[03:54] LECTURER: He decorated each floor differently, and the interior is now regarded by many as a work of art. The house has become a museum that receives about 200,000 visitors each year.\n" +
                    "[04:12] LECTURER: Family portraits remain on the ground floor, alongside drawings Hugo made during his travels. [Q35] Other rooms contain large tapestries that he designed or valued.\n" +
                    "[04:33] LECTURER: Hugo created decorative wooden wall panels himself. He bought inexpensive wooden furniture at a market and transformed it into detailed carvings. [Q36]\n" +
                    "[04:55] LECTURER: These dark wooden decorations give the lower floor a serious, shadowy atmosphere.\n" +
                    "[05:03] LECTURER: On the next floor, there are two impressive lounges used for entertaining guests. One is decorated mainly in red, while the other uses blue.\n" +
                    "[05:12] LECTURER: Chinese design influenced the wallpaper and lamps. Hugo made the lamps himself by copying existing examples. [Q37]\n" +
                    "[05:27] LECTURER: His library contains many of his favourite books and forms part of the passage leading to the third floor. It was a comfortable place where he could relax during the afternoon.\n" +
                    "[05:40] LECTURER: At the top of the house is a room known as the Lookout because it overlooks the harbour. [Q38] Unlike the darker rooms below, it is bright and resembles a glass office.\n" +
                    "[05:58] LECTURER: Hugo often worked at his desk there and continued writing until lunchtime.\n" +
                    "[06:06] LECTURER: Although Hugo was a talented writer, he also cared deeply about social issues. He entertained well-known writers at his home, but he also invited many disadvantaged local children to dinner once a week. [Q39]\n" +
                    "[06:30] LECTURER: In 1927, Hugo's relatives owned the house and decided to donate it to the city of Paris. [Q40]\n" +
                    "[06:47] LECTURER: The building was later restored with the help of historical photographs. Today, it remains a museum open to the public."
            )
        )

        val questions = listOf(
            ListeningQuestionDetailDto(
                id = "cam18_t4_q1",
                position = 1,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Role: 1 ________\nâ€¢ Location: Fordham [ 2 ] Centre, [ 3 ] Road, Fordham",
                options = null,
                sectionNumber = 1,
                timestampStart = 106,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q2",
                position = 2,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Role: [ 1 ]\nâ€¢ Location: Fordham 2 ________ Centre, [ 3 ] Road, Fordham\nâ€¢ Work involves:\n  â€“ dealing with enquiries",
                options = null,
                sectionNumber = 1,
                timestampStart = 122,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q3",
                position = 3,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Location: Fordham [ 2 ] Centre, 3 ________ Road, Fordham\nâ€¢ Work involves:\n  â€“ dealing with enquiries\n  â€“ making [ 4 ] and reorganising them",
                options = null,
                sectionNumber = 1,
                timestampStart = 134,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q4",
                position = 4,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Work involves:\n  â€“ dealing with enquiries\n  â€“ making 4 ________ and reorganising them\n  â€“ maintaining the internal [ 5 ]\n  â€“ general administration",
                options = null,
                sectionNumber = 1,
                timestampStart = 158,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q5",
                position = 5,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Work involves:\n  â€“ making [ 4 ] and reorganising them\n  â€“ maintaining the internal 5 ________\n  â€“ general administration\nâ€¢ Requirements:\n  â€“ [ 6 ] (essential)",
                options = null,
                sectionNumber = 1,
                timestampStart = 169,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q6",
                position = 6,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Requirements:\n  â€“ 6 ________ (essential)\n  â€“ a calm and [ 7 ] manner\n  â€“ good IT skills",
                options = null,
                sectionNumber = 1,
                timestampStart = 238,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q7",
                position = 7,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Requirements:\n  â€“ [ 6 ] (essential)\n  â€“ a calm and 7 ________ manner\n  â€“ good IT skills\nâ€¢ Other information:\n  â€“ a [ 8 ] job",
                options = null,
                sectionNumber = 1,
                timestampStart = 258,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q8",
                position = 8,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Other information:\n  â€“ a 8 ________ job â€“ further opportunities may be available\n  â€“ hours: 7.45 a.m. to [ 9 ] p.m. Monday to Friday\n  â€“ [ 10 ] is available onsite",
                options = null,
                sectionNumber = 1,
                timestampStart = 303,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q9",
                position = 9,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Other information:\n  â€“ a [ 8 ] job â€“ further opportunities may be available\n  â€“ hours: 7.45 a.m. to 9 ________ p.m. Monday to Friday\n  â€“ [ 10 ] is available onsite",
                options = null,
                sectionNumber = 1,
                timestampStart = 337,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q10",
                position = 10,
                type = "FILL_IN_BLANK",
                prompt = "Job details from employment agency\nComplete the notes below. Write ONE WORD AND/OR A NUMBER for each answer.\n\nâ€¢ Other information:\n  â€“ hours: 7.45 a.m. to [ 9 ] p.m. Monday to Friday\n  â€“ 10 ________ is available onsite",
                options = null,
                sectionNumber = 1,
                timestampStart = 348,
                timestampEnd = 0
            ),
            // Section 2: Q11-20
            ListeningQuestionDetailDto(
                id = "cam18_t4_q11",
                position = 11,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 11–14: Choose the correct letter, A, B or C.\n\n11. The museum building was originally",
                options = listOf(
                    "A. a factory.",
                    "B. a private home.",
                    "C. a hall of residence."
                ),
                sectionNumber = 2,
                timestampStart = 67,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q12",
                position = 12,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 11–14: Choose the correct letter, A, B or C.\n\n12. The university uses part of the museum building as",
                options = listOf(
                    "A. teaching rooms.",
                    "B. a research library.",
                    "C. administration offices."
                ),
                sectionNumber = 2,
                timestampStart = 101,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q13",
                position = 13,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 11–14: Choose the correct letter, A, B or C.\n\n13. What does the guide say about the entrance fee?",
                options = listOf(
                    "A. Visitors decide whether or not they wish to pay.",
                    "B. Only some parts of the museum are free.",
                    "C. It is cheaper for students and children."
                ),
                sectionNumber = 2,
                timestampStart = 144,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q14",
                position = 14,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 11–14: Choose the correct letter, A, B or C.\n\n14. What are visitors advised to leave in the cloakroom?",
                options = listOf(
                    "A. cameras",
                    "B. coats",
                    "C. bags"
                ),
                sectionNumber = 2,
                timestampStart = 176,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q15",
                position = 15,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 15–20: What does the guide say about each of the following areas of the museum?\nChoose SIX answers from the box and write the correct letter, A–H, next to Questions 15–20.\n\nFeatures:\nA. Parents must supervise their children.\nB. There are new things to see.\nC. It is closed today.\nD. This is only for school groups.\nE. There is a quiz for visitors.\nF. It features something created by students.\nG. An expert is here today.\nH. There is a one-way system.\n\n15. Four Seasons",
                options = listOf(
                    "A. Parents must supervise their children.",
                    "B. There are new things to see.",
                    "C. It is closed today.",
                    "D. This is only for school groups.",
                    "E. There is a quiz for visitors.",
                    "F. It features something created by students.",
                    "G. An expert is here today.",
                    "H. There is a one-way system."
                ),
                sectionNumber = 2,
                timestampStart = 246,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q16",
                position = 16,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 15–20: What does the guide say about each of the following areas of the museum?\nChoose SIX answers from the box and write the correct letter, A–H, next to Questions 15–20.\n\nFeatures:\nA. Parents must supervise their children.\nB. There are new things to see.\nC. It is closed today.\nD. This is only for school groups.\nE. There is a quiz for visitors.\nF. It features something created by students.\nG. An expert is here today.\nH. There is a one-way system.\n\n16. Farmhouse Kitchen",
                options = listOf(
                    "A. Parents must supervise their children.",
                    "B. There are new things to see.",
                    "C. It is closed today.",
                    "D. This is only for school groups.",
                    "E. There is a quiz for visitors.",
                    "F. It features something created by students.",
                    "G. An expert is here today.",
                    "H. There is a one-way system."
                ),
                sectionNumber = 2,
                timestampStart = 290,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q17",
                position = 17,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 15–20: What does the guide say about each of the following areas of the museum?\nChoose SIX answers from the box and write the correct letter, A–H, next to Questions 15–20.\n\nFeatures:\nA. Parents must supervise their children.\nB. There are new things to see.\nC. It is closed today.\nD. This is only for school groups.\nE. There is a quiz for visitors.\nF. It features something created by students.\nG. An expert is here today.\nH. There is a one-way system.\n\n17. A Year on the Farm",
                options = listOf(
                    "A. Parents must supervise their children.",
                    "B. There are new things to see.",
                    "C. It is closed today.",
                    "D. This is only for school groups.",
                    "E. There is a quiz for visitors.",
                    "F. It features something created by students.",
                    "G. An expert is here today.",
                    "H. There is a one-way system."
                ),
                sectionNumber = 2,
                timestampStart = 305,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q18",
                position = 18,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 15–20: What does the guide say about each of the following areas of the museum?\nChoose SIX answers from the box and write the correct letter, A–H, next to Questions 15–20.\n\nFeatures:\nA. Parents must supervise their children.\nB. There are new things to see.\nC. It is closed today.\nD. This is only for school groups.\nE. There is a quiz for visitors.\nF. It features something created by students.\nG. An expert is here today.\nH. There is a one-way system.\n\n18. Wagon Walk",
                options = listOf(
                    "A. Parents must supervise their children.",
                    "B. There are new things to see.",
                    "C. It is closed today.",
                    "D. This is only for school groups.",
                    "E. There is a quiz for visitors.",
                    "F. It features something created by students.",
                    "G. An expert is here today.",
                    "H. There is a one-way system."
                ),
                sectionNumber = 2,
                timestampStart = 350,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q19",
                position = 19,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 15–20: What does the guide say about each of the following areas of the museum?\nChoose SIX answers from the box and write the correct letter, A–H, next to Questions 15–20.\n\nFeatures:\nA. Parents must supervise their children.\nB. There are new things to see.\nC. It is closed today.\nD. This is only for school groups.\nE. There is a quiz for visitors.\nF. It features something created by students.\nG. An expert is here today.\nH. There is a one-way system.\n\n19. Bees are Magic",
                options = listOf(
                    "A. Parents must supervise their children.",
                    "B. There are new things to see.",
                    "C. It is closed today.",
                    "D. This is only for school groups.",
                    "E. There is a quiz for visitors.",
                    "F. It features something created by students.",
                    "G. An expert is here today.",
                    "H. There is a one-way system."
                ),
                sectionNumber = 2,
                timestampStart = 366,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q20",
                position = 20,
                type = "MULTIPLE_CHOICE",
                prompt = "The Museum of Farming Life\nQuestions 15–20: What does the guide say about each of the following areas of the museum?\nChoose SIX answers from the box and write the correct letter, A–H, next to Questions 15–20.\n\nFeatures:\nA. Parents must supervise their children.\nB. There are new things to see.\nC. It is closed today.\nD. This is only for school groups.\nE. There is a quiz for visitors.\nF. It features something created by students.\nG. An expert is here today.\nH. There is a one-way system.\n\n20. The Pond",
                options = listOf(
                    "A. Parents must supervise their children.",
                    "B. There are new things to see.",
                    "C. It is closed today.",
                    "D. This is only for school groups.",
                    "E. There is a quiz for visitors.",
                    "F. It features something created by students.",
                    "G. An expert is here today.",
                    "H. There is a one-way system."
                ),
                sectionNumber = 2,
                timestampStart = 379,
                timestampEnd = 0
            ),
            // Section 3: Q21-30
            ListeningQuestionDetailDto(
                id = "cam18_t4_q21",
                position = 21,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 21 and 22: Choose TWO letters, A–E.\nWhich TWO educational skills were shown in the video of children doing origami?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf(
                    "A. solving problems",
                    "B. following instructions",
                    "C. working cooperatively",
                    "D. learning through play",
                    "E. developing hand-eye coordination"
                ),
                sectionNumber = 3,
                timestampStart = 88,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q22",
                position = 22,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 21 and 22: Choose TWO letters, A–E.\nWhich TWO educational skills were shown in the video of children doing origami?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf(
                    "A. solving problems",
                    "B. following instructions",
                    "C. working cooperatively",
                    "D. learning through play",
                    "E. developing hand-eye coordination"
                ),
                sectionNumber = 3,
                timestampStart = 123,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q23",
                position = 23,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 23–27: Which comment do the students make about each of the following children in the video?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 23–27.\n\nComments:\nA. demonstrated independence\nB. was distracted by other children\nC. developed a competitive attitude\nD. seemed to find the activity calming\nE. asked the teacher for help\nF. seemed confused\nG. seemed to find the activity easy\n\n23. Sid",
                options = listOf(
                    "A. demonstrated independence",
                    "B. was distracted by other children",
                    "C. developed a competitive attitude",
                    "D. seemed to find the activity calming",
                    "E. asked the teacher for help",
                    "F. seemed confused",
                    "G. seemed to find the activity easy"
                ),
                sectionNumber = 3,
                timestampStart = 157,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q24",
                position = 24,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 23–27: Which comment do the students make about each of the following children in the video?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 23–27.\n\nComments:\nA. demonstrated independence\nB. was distracted by other children\nC. developed a competitive attitude\nD. seemed to find the activity calming\nE. asked the teacher for help\nF. seemed confused\nG. seemed to find the activity easy\n\n24. Jack",
                options = listOf(
                    "A. demonstrated independence",
                    "B. was distracted by other children",
                    "C. developed a competitive attitude",
                    "D. seemed to find the activity calming",
                    "E. asked the teacher for help",
                    "F. seemed confused",
                    "G. seemed to find the activity easy"
                ),
                sectionNumber = 3,
                timestampStart = 171,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q25",
                position = 25,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 23–27: Which comment do the students make about each of the following children in the video?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 23–27.\n\nComments:\nA. demonstrated independence\nB. was distracted by other children\nC. developed a competitive attitude\nD. seemed to find the activity calming\nE. asked the teacher for help\nF. seemed confused\nG. seemed to find the activity easy\n\n25. Naomi",
                options = listOf(
                    "A. demonstrated independence",
                    "B. was distracted by other children",
                    "C. developed a competitive attitude",
                    "D. seemed to find the activity calming",
                    "E. asked the teacher for help",
                    "F. seemed confused",
                    "G. seemed to find the activity easy"
                ),
                sectionNumber = 3,
                timestampStart = 186,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q26",
                position = 26,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 23–27: Which comment do the students make about each of the following children in the video?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 23–27.\n\nComments:\nA. demonstrated independence\nB. was distracted by other children\nC. developed a competitive attitude\nD. seemed to find the activity calming\nE. asked the teacher for help\nF. seemed confused\nG. seemed to find the activity easy\n\n26. Anya",
                options = listOf(
                    "A. demonstrated independence",
                    "B. was distracted by other children",
                    "C. developed a competitive attitude",
                    "D. seemed to find the activity calming",
                    "E. asked the teacher for help",
                    "F. seemed confused",
                    "G. seemed to find the activity easy"
                ),
                sectionNumber = 3,
                timestampStart = 198,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q27",
                position = 27,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 23–27: Which comment do the students make about each of the following children in the video?\nChoose FIVE answers from the box and write the correct letter, A–G, next to Questions 23–27.\n\nComments:\nA. demonstrated independence\nB. was distracted by other children\nC. developed a competitive attitude\nD. seemed to find the activity calming\nE. asked the teacher for help\nF. seemed confused\nG. seemed to find the activity easy\n\n27. Zara",
                options = listOf(
                    "A. demonstrated independence",
                    "B. was distracted by other children",
                    "C. developed a competitive attitude",
                    "D. seemed to find the activity calming",
                    "E. asked the teacher for help",
                    "F. seemed confused",
                    "G. seemed to find the activity easy"
                ),
                sectionNumber = 3,
                timestampStart = 214,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q28",
                position = 28,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 28–30: Choose the correct letter, A, B or C.\n\n28. Before starting an origami activity in class, the students think it is important for the teacher to",
                options = listOf(
                    "A. make models that demonstrate the different stages.",
                    "B. check that the children have the right materials.",
                    "C. demonstrate how to fold a simple shape."
                ),
                sectionNumber = 3,
                timestampStart = 286,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q29",
                position = 29,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 28–30: Choose the correct letter, A, B or C.\n\n29. The students agree that some teachers might be unwilling to use origami in class because",
                options = listOf(
                    "A. they consider it takes up too much lesson time.",
                    "B. they may not have the necessary skills.",
                    "C. they think modern children prefer digital activities."
                ),
                sectionNumber = 3,
                timestampStart = 323,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q30",
                position = 30,
                type = "MULTIPLE_CHOICE",
                prompt = "Origami as an educational tool\nQuestions 28–30: Choose the correct letter, A, B or C.\n\n30. Why do the students decide to use origami in their maths teaching practice?",
                options = listOf(
                    "A. to correct a common misunderstanding",
                    "B. to revise a topic taught recently",
                    "C. to introduce a new concept"
                ),
                sectionNumber = 3,
                timestampStart = 350,
                timestampEnd = 0
            ),
            // Section 4: Q31-40
            ListeningQuestionDetailDto(
                id = "cam18_t4_q31",
                position = 31,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nHis life\n• 1802: Hugo was born\n• 1862: Hugo's novel Les Misérables was published\n  - even people who have not read it know its 31 ________\n• Hugo used his fame to oppose [ 32 ] and also tried to reform the educational system",
                options = null,
                sectionNumber = 4,
                timestampStart = 101,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q32",
                position = 32,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nHis life\n• 1862: Hugo's novel Les Misérables was published\n  - even people who have not read it know its [ 31 ]\n• Hugo used his fame to oppose 32 ________ and also tried to reform the educational system\n• 1851: Hugo had to flee from Paris and went to live in other parts of [ 33 ]",
                options = null,
                sectionNumber = 4,
                timestampStart = 123,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q33",
                position = 33,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nHis life\n• Hugo used his fame to oppose [ 32 ] and also tried to reform the educational system\n• 1851: Hugo had to flee from Paris and went to live in other parts of 33 ________\n• 1855: Hugo bought Hauteville House on Guernsey with money from a successful volume of [ 34 ]",
                options = null,
                sectionNumber = 4,
                timestampStart = 176,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q34",
                position = 34,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nHis life\n• 1851: Hugo had to flee from Paris and went to live in other parts of [ 33 ]\n• 1855: Hugo bought Hauteville House on Guernsey with money from a successful volume of 34 ________\n\nHauteville House\n• Decorated each floor in a different style\n• Ground floor: contains family portraits and Hugo's own [ 35 ]",
                options = null,
                sectionNumber = 4,
                timestampStart = 194,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q35",
                position = 35,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nHauteville House\n• Decorated each floor in a different style\n• Ground floor: contains family portraits and Hugo's own 35 ________\n• Lower floor: wall panels made of cheap wooden [ 36 ] which Hugo carved himself",
                options = null,
                sectionNumber = 4,
                timestampStart = 252,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q36",
                position = 36,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nHauteville House\n• Ground floor: contains family portraits and Hugo's own [ 35 ]\n• Lower floor: wall panels made of cheap wooden 36 ________ which Hugo carved himself\n• Next floor:\n  - two lounges\n  - Chinese wallpaper\n  - [ 37 ] made by Hugo",
                options = null,
                sectionNumber = 4,
                timestampStart = 273,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q37",
                position = 37,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nHauteville House\n• Lower floor: wall panels made of cheap wooden [ 36 ] which Hugo carved himself\n• Next floor:\n  - two lounges\n  - Chinese wallpaper\n  - 37 ________ made by Hugo\n• Top floor:\n  - Hugo's writing room is called the 'Lookout'\n  - has a view of the [ 38 ]",
                options = null,
                sectionNumber = 4,
                timestampStart = 312,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q38",
                position = 38,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nHauteville House\n• Next floor:\n  - two lounges\n  - Chinese wallpaper\n  - [ 37 ] made by Hugo\n• Top floor:\n  - Hugo's writing room is called the 'Lookout'\n  - has a view of the 38 ________\n\nLater years\n• Held regular dinners at home for poor local [ 39 ]",
                options = null,
                sectionNumber = 4,
                timestampStart = 340,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q39",
                position = 39,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nHauteville House\n• Top floor:\n  - Hugo's writing room is called the 'Lookout'\n  - has a view of the [ 38 ]\n\nLater years\n• Held regular dinners at home for poor local 39 ________\n• 1927: Hauteville House was given to Paris by Hugo's [ 40 ]",
                options = null,
                sectionNumber = 4,
                timestampStart = 366,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t4_q40",
                position = 40,
                type = "FILL_IN_BLANK",
                prompt = "Victor Hugo\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nLater years\n• Held regular dinners at home for poor local [ 39 ]\n• 1927: Hauteville House was given to Paris by Hugo's 40 ________",
                options = null,
                sectionNumber = 4,
                timestampStart = 390,
                timestampEnd = 0
            )
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
