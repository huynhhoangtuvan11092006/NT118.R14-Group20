package com.example.ielts.data.datasource

import com.example.ielts.data.remote.dto.ListeningQuestionDetailDto
import com.example.ielts.data.remote.dto.ListeningSectionDto
import com.example.ielts.data.remote.dto.ListeningTestDetailDto

object CambridgeListeningTestDetailDataSource {

    fun getTestDetail(testId: String, testTitle: String? = null): ListeningTestDetailDto {
        val allTests = CambridgeListeningDataSource.getAllTests()
        val testItem = allTests.find { it.id == testId }
            ?: allTests.find { testTitle != null && it.title.equals(testTitle, ignoreCase = true) }
            ?: if (testId.contains("test2") || testTitle?.contains("Test 2") == true) {
                allTests.find { it.id == "cam18-listening-test2" } ?: allTests.first()
            } else if (testId.contains("18") || testTitle?.contains("18") == true) {
                allTests.find { it.id == "cam18-listening-test1" } ?: allTests.first()
            } else {
                allTests.first()
            }

        return if (testItem.id == "cam18-listening-test2" || testTitle?.contains("Test 2") == true || testId.contains("test2")) {
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
                durationSeconds = 420,
                transcript = "[00:00] Speaker: Good morning and welcome to this presentation on the proposed new housing development in the local district."
            ),
            ListeningSectionDto(
                sectionNumber = 3,
                title = "Part 3: The Laki Eruption of 1783",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758609/%E5%89%9118_section2-part1.mp3",
                durationSeconds = 420,
                transcript = "[00:00] Students: Today we will present our research findings on the historical and climatic impacts of the 1783 Laki volcanic eruption in Iceland."
            ),
            ListeningSectionDto(
                sectionNumber = 4,
                title = "Part 4: Pockets",
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758609/%E5%89%9118_section2-part1.mp3",
                durationSeconds = 450,
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
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q2",
                position = 2,
                type = "FILL_IN_BLANK",
                prompt = "Working at Milo's Restaurants\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nBenefits\n• [ 1 ] provided for all staff\n• 2 ________ during weekdays at all Milo's Restaurants\n• [ 3 ] provided after midnight\n\nPerson specification\n• must be prepared to work well in a team\n• must care about maintaining a high standard of [ 4 ]\n• must have a qualification in [ 5 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 115,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q3",
                position = 3,
                type = "FILL_IN_BLANK",
                prompt = "Working at Milo's Restaurants\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nBenefits\n• [ 1 ] provided for all staff\n• [ 2 ] during weekdays at all Milo's Restaurants\n• 3 ________ provided after midnight\n\nPerson specification\n• must be prepared to work well in a team\n• must care about maintaining a high standard of [ 4 ]\n• must have a qualification in [ 5 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 136,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q4",
                position = 4,
                type = "FILL_IN_BLANK",
                prompt = "Working at Milo's Restaurants\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nBenefits\n• [ 1 ] provided for all staff\n• [ 2 ] during weekdays at all Milo's Restaurants\n• [ 3 ] provided after midnight\n\nPerson specification\n• must be prepared to work well in a team\n• must care about maintaining a high standard of 4 ________\n• must have a qualification in [ 5 ]",
                options = null,
                sectionNumber = 1,
                timestampStart = 172,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q5",
                position = 5,
                type = "FILL_IN_BLANK",
                prompt = "Working at Milo's Restaurants\nComplete the notes below.\nWrite ONE WORD ONLY for each answer.\n\nBenefits\n• [ 1 ] provided for all staff\n• [ 2 ] during weekdays at all Milo's Restaurants\n• [ 3 ] provided after midnight\n\nPerson specification\n• must be prepared to work well in a team\n• must care about maintaining a high standard of [ 4 ]\n• must have a qualification in 5 ________",
                options = null,
                sectionNumber = 1,
                timestampStart = 204,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q6",
                position = 6,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: 6 ________ Street\nJob title: Breakfast supervisor\nResponsibilities include:\n• Checking portions, etc. are correct\n• Making sure [ 7 ] is clean\nPay and conditions:\n• Starting salary [ 8 ] £ per hour\n• Start work at 5.30 a.m.",
                options = null,
                sectionNumber = 1,
                timestampStart = 254,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q7",
                position = 7,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: [ 6 ] Street\nJob title: Breakfast supervisor\nResponsibilities include:\n• Checking portions, etc. are correct\n• Making sure 7 ________ is clean\nPay and conditions:\n• Starting salary [ 8 ] £ per hour\n• Start work at 5.30 a.m.",
                options = null,
                sectionNumber = 1,
                timestampStart = 288,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q8",
                position = 8,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: [ 6 ] Street\nJob title: Breakfast supervisor\nResponsibilities include:\n• Checking portions, etc. are correct\n• Making sure [ 7 ] is clean\nPay and conditions:\n• Starting salary 8 ________ £ per hour\n• Start work at 5.30 a.m.",
                options = null,
                sectionNumber = 1,
                timestampStart = 316,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q9",
                position = 9,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: City Road\nJob title: Junior chef\nResponsibilities include:\n• Supporting senior chefs\n• Maintaining stock and organizing 9 ________\nPay and conditions:\n• Annual salary £23,000\n• No work on a [ 10 ] once a month",
                options = null,
                sectionNumber = 1,
                timestampStart = 361,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q10",
                position = 10,
                type = "FILL_IN_BLANK",
                prompt = "Jobs available\nComplete the table below.\nWrite ONE WORD AND/OR A NUMBER for each answer.\n\nLocation: City Road\nJob title: Junior chef\nResponsibilities include:\n• Supporting senior chefs\n• Maintaining stock and organizing [ 9 ]\nPay and conditions:\n• Annual salary £23,000\n• No work on a 10 ________ once a month",
                options = null,
                sectionNumber = 1,
                timestampStart = 441,
                timestampEnd = 0
            ),

            // ==================== PART 2: Planned Housing Development ====================
            ListeningQuestionDetailDto(
                id = "cam18_t2_q11",
                position = 11,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 11 and 12: Choose TWO letters, A–E.\nWhich TWO groups of people are particularly encouraged to attend the meeting?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. local business owners", "B. young families", "C. retired people", "D. school teachers", "E. university students"),
                sectionNumber = 2,
                timestampStart = 15,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q12",
                position = 12,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 11 and 12: Choose TWO letters, A–E.\nWhich TWO groups of people are particularly encouraged to attend the meeting?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. local business owners", "B. young families", "C. retired people", "D. school teachers", "E. university students"),
                sectionNumber = 2,
                timestampStart = 30,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q13",
                position = 13,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 13 and 14: Choose TWO letters, A–E.\nWhich TWO facilities are included in the new housing proposal?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. a medical centre", "B. a community centre", "C. a public library", "D. a swimming pool", "E. a sports pavilion"),
                sectionNumber = 2,
                timestampStart = 45,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q14",
                position = 14,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 13 and 14: Choose TWO letters, A–E.\nWhich TWO facilities are included in the new housing proposal?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. a medical centre", "B. a community centre", "C. a public library", "D. a swimming pool", "E. a sports pavilion"),
                sectionNumber = 2,
                timestampStart = 60,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q15",
                position = 15,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Map / Plan labelling\n15. Solar panels location",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H"),
                sectionNumber = 2,
                timestampStart = 75,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q16",
                position = 16,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Map / Plan labelling\n16. Children's playground",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H"),
                sectionNumber = 2,
                timestampStart = 90,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q17",
                position = 17,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Map / Plan labelling\n17. Park area",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H"),
                sectionNumber = 2,
                timestampStart = 105,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q18",
                position = 18,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Map / Plan labelling\n18. Community garden",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H"),
                sectionNumber = 2,
                timestampStart = 120,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q19",
                position = 19,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Map / Plan labelling\n19. Bicycle path entrance",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H"),
                sectionNumber = 2,
                timestampStart = 135,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q20",
                position = 20,
                type = "MULTIPLE_CHOICE",
                prompt = "Planned Housing Development\nQuestions 15–20: Map / Plan labelling\n20. Car parking area",
                options = listOf("A", "B", "C", "D", "E", "F", "G", "H"),
                sectionNumber = 2,
                timestampStart = 150,
                timestampEnd = 0
            ),

            // ==================== PART 3: The Laki Eruption of 1783 ====================
            ListeningQuestionDetailDto(
                id = "cam18_t2_q21",
                position = 21,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 21–24: Choose the correct letter, A, B or C.\n\n21. What initially surprised the students about the Laki eruption?",
                options = listOf("A. the sheer volume of lava produced", "B. the number of countries affected", "C. the duration of the eruption period"),
                sectionNumber = 3,
                timestampStart = 15,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q22",
                position = 22,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 21–24: Choose the correct letter, A, B or C.\n\n22. What impact did the poisonous haze have on European agriculture?",
                options = listOf("A. crop yields dropped drastically across the continent", "B. livestock adapted better than expected", "C. farming practices changed immediately"),
                sectionNumber = 3,
                timestampStart = 30,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q23",
                position = 23,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 21–24: Choose the correct letter, A, B or C.\n\n23. What did historical documents reveal about public health in 1783?",
                options = listOf("A. Doctors identified the cause promptly.", "B. Respiratory illness surged dramatically.", "C. Most deaths were among young children."),
                sectionNumber = 3,
                timestampStart = 45,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q24",
                position = 24,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 21–24: Choose the correct letter, A, B or C.\n\n24. How did the students decide to structure their presentation?",
                options = listOf("A. by geographic region", "B. chronologically by year", "C. by scientific discipline"),
                sectionNumber = 3,
                timestampStart = 60,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q25",
                position = 25,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 25 and 26: Choose TWO letters, A–E.\nWhich TWO historical consequences of the eruption do the students agree to emphasize?\n(Chọn lựa chọn thứ nhất trong 2 đáp án)",
                options = listOf("A. famine leading to social unrest", "B. climatic cooling in North America", "C. political revolutions", "D. technological inventions", "E. mass migration to cities"),
                sectionNumber = 3,
                timestampStart = 75,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q26",
                position = 26,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 25 and 26: Choose TWO letters, A–E.\nWhich TWO historical consequences of the eruption do the students agree to emphasize?\n(Chọn lựa chọn thứ hai trong 2 đáp án)",
                options = listOf("A. famine leading to social unrest", "B. climatic cooling in North America", "C. political revolutions", "D. technological inventions", "E. mass migration to cities"),
                sectionNumber = 3,
                timestampStart = 90,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q27",
                position = 27,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 27–30: Matching\n27. Impact on ice core samples",
                options = listOf("A", "B", "C", "D", "E", "F"),
                sectionNumber = 3,
                timestampStart = 105,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q28",
                position = 28,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 27–30: Matching\n28. Tree ring density studies",
                options = listOf("A", "B", "C", "D", "E", "F"),
                sectionNumber = 3,
                timestampStart = 120,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q29",
                position = 29,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 27–30: Matching\n29. Sulfate aerosol records",
                options = listOf("A", "B", "C", "D", "E", "F"),
                sectionNumber = 3,
                timestampStart = 135,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q30",
                position = 30,
                type = "MULTIPLE_CHOICE",
                prompt = "The Laki Eruption of 1783\nQuestions 27–30: Matching\n30. Global temperature estimations",
                options = listOf("A", "B", "C", "D", "E", "F"),
                sectionNumber = 3,
                timestampStart = 150,
                timestampEnd = 0
            ),

            // ==================== PART 4: Pockets ====================
            ListeningQuestionDetailDto(
                id = "cam18_t2_q31",
                position = 31,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\nComplete the notes below. Write ONE WORD ONLY for each answer.\n\nEarly history of pockets\n• pockets were initially 31 ________ rather than stylish",
                options = null,
                sectionNumber = 4,
                timestampStart = 15,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q32",
                position = 32,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\n• sewn inside men's [ 32 ] for security",
                options = null,
                sectionNumber = 4,
                timestampStart = 30,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q33",
                position = 33,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\n• customized by a local 33 ________ according to personal preference",
                options = null,
                sectionNumber = 4,
                timestampStart = 45,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q34",
                position = 34,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\n• pocket designs reflected the owner's 34 ________ and status",
                options = null,
                sectionNumber = 4,
                timestampStart = 60,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q35",
                position = 35,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\n• in women's clothing, pockets were not initially 35 ________ from the outside",
                options = null,
                sectionNumber = 4,
                timestampStart = 75,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q36",
                position = 36,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\n• bags were tied with a 36 ________ under petticoats",
                options = null,
                sectionNumber = 4,
                timestampStart = 90,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q37",
                position = 37,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\n• fastened around the 37 ________ for ease of carrying",
                options = null,
                sectionNumber = 4,
                timestampStart = 105,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q38",
                position = 38,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\n• used to hold small items such as keys, coins, and 38 ________",
                options = null,
                sectionNumber = 4,
                timestampStart = 120,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q39",
                position = 39,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\n• changes in fashion silhouettes prioritized body 39 ________ over functionality",
                options = null,
                sectionNumber = 4,
                timestampStart = 135,
                timestampEnd = 0
            ),
            ListeningQuestionDetailDto(
                id = "cam18_t2_q40",
                position = 40,
                type = "FILL_IN_BLANK",
                prompt = "Pockets\n• led to the emergence of the modern 40 ________ as an indispensable accessory",
                options = null,
                sectionNumber = 4,
                timestampStart = 150,
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
