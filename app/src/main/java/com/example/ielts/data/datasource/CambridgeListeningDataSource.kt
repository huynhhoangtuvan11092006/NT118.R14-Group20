package com.example.ielts.data.datasource

import com.example.ielts.data.remote.dto.ListeningTestListItemDto

object CambridgeListeningDataSource {

    fun getAllTests(): List<ListeningTestListItemDto> {
        val list = mutableListOf<ListeningTestListItemDto>()

        // ==================== CAMBRIDGE 19 (2024) ====================
        list.add(
            ListeningTestListItemDto(
                id = "cam19-listening-test1",
                title = "Cambridge IELTS 19 - Listening Test 1",
                summary = "Bộ đề thi thử IELTS Listening chuẩn khảo thí trích từ Cambridge IELTS 19 (2024)",
                difficulty = "Trung bình",
                durationMinutes = 35,
                totalAudioDuration = 1820,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Hotel Reservation & Conference Facilities",
                    "Section 2: Walking Tour of Riverford Historic Town",
                    "Section 3: Students Tutorial on Sustainable Architecture",
                    "Section 4: Academic Lecture on History of Early Photography"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793759097/Test1_Part1.mp3",
                userProgress = null
            )
        )
        list.add(
            ListeningTestListItemDto(
                id = "cam19-listening-test2",
                title = "Cambridge IELTS 19 - Listening Test 2",
                summary = "Bộ đề thi thử IELTS Listening chuẩn khảo thí trích từ Cambridge IELTS 19 (2024)",
                difficulty = "Khó",
                durationMinutes = 35,
                totalAudioDuration = 1835,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Car Rental & Breakdown Insurance",
                    "Section 2: Volunteer Community Garden Project",
                    "Section 3: Discussion on Renewable Energy in Agriculture",
                    "Section 4: Psychology Lecture on Memory and Attention Span"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793759241/Test2_Part1.mp3",
                userProgress = null
            )
        )
        list.add(
            ListeningTestListItemDto(
                id = "cam19-listening-test3",
                title = "Cambridge IELTS 19 - Listening Test 3",
                summary = "Bộ đề thi thử IELTS Listening chuẩn khảo thí trích từ Cambridge IELTS 19 (2024)",
                difficulty = "Trung bình",
                durationMinutes = 35,
                totalAudioDuration = 1810,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Music Festival Ticket Booking",
                    "Section 2: City Library Services & Digital Resources",
                    "Section 3: Research Proposal on Deep-Sea Bioluminescence",
                    "Section 4: History Lecture on Ancient Silk Road Trade"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793759309/Test3_Part1.mp3",
                userProgress = null
            )
        )
        list.add(
            ListeningTestListItemDto(
                id = "cam19-listening-test4",
                title = "Cambridge IELTS 19 - Listening Test 4",
                summary = "Bộ đề thi thử IELTS Listening chuẩn khảo thí trích từ Cambridge IELTS 19 (2024)",
                difficulty = "Khó",
                durationMinutes = 35,
                totalAudioDuration = 1850,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Job Application at a Winter Ski Resort",
                    "Section 2: Guide to the National Maritime Museum",
                    "Section 3: Educational Case Study on Childhood Bilingualism",
                    "Section 4: Environmental Science on Soil Erosion & Conservation"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793759381/Test4_Part1.mp3",
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 18 (2023) ====================
        list.add(
            ListeningTestListItemDto(
                id = "cam18-listening-test1",
                title = "Cambridge IELTS 18 - Listening Test 1",
                summary = "Bộ đề thi thử IELTS Listening chuẩn khảo thí trích từ Cambridge IELTS 18 Test 1",
                difficulty = "Standard IELTS",
                durationMinutes = 35,
                totalAudioDuration = 1845,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Moving Company & Transport Inquiry",
                    "Section 2: Tour of the Sports Gym & Community Center",
                    "Section 3: Students Discussion on Renewable Energy Project",
                    "Section 4: University Lecture on Marine Biology & Cetaceans"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793754902/%E5%89%9118_section1-part1.mp3",
                userProgress = null
            )
        )
        list.add(
            ListeningTestListItemDto(
                id = "cam18-listening-test2",
                title = "Cambridge IELTS 18 - Listening Test 2",
                summary = "Bộ đề thi thử IELTS Listening trích từ Cambridge IELTS 18 Academic & General",
                difficulty = "Trung bình",
                durationMinutes = 35,
                totalAudioDuration = 1830,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Public Health Clinic Registration & Checkup",
                    "Section 2: New Residents Guide to City Waste & Recycling",
                    "Section 3: Engineering Review on Surgical Robotics",
                    "Section 4: Anthropology Lecture on Prehistoric Cave Art"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758609/%E5%89%9118_section2-part1.mp3",
                userProgress = null
            )
        )
        list.add(
            ListeningTestListItemDto(
                id = "cam18-listening-test3",
                title = "Cambridge IELTS 18 - Listening Test 3",
                summary = "Bộ đề thi thử IELTS Listening trích từ Cambridge IELTS 18 Academic & General",
                difficulty = "Trung bình",
                durationMinutes = 35,
                totalAudioDuration = 1825,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Bicycle Rental and Scenic Cycling Route Inquiry",
                    "Section 2: Summer Youth Sailing Club Activities & Safety",
                    "Section 3: Economics Seminar on Microfinance in Developing Nations",
                    "Section 4: Zoology Lecture on Arctic Fox Adaptation & Survival"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758712/%E5%89%9118_section3_part1.mp3",
                userProgress = null
            )
        )
        list.add(
            ListeningTestListItemDto(
                id = "cam18-listening-test4",
                title = "Cambridge IELTS 18 - Listening Test 4",
                summary = "Bộ đề thi thử IELTS Listening trích từ Cambridge IELTS 18 Academic & General",
                difficulty = "Khó",
                durationMinutes = 35,
                totalAudioDuration = 1840,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: University Campus Accommodation Application",
                    "Section 2: Local Art Gallery Modern Exhibition Tour",
                    "Section 3: Geography Students Coastline Field Trip Planning",
                    "Section 4: Engineering Lecture on Carbon Capture & Storage"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793758844/%E5%89%9118_section4_part1.mp3",
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 17 ====================
        list.add(
            ListeningTestListItemDto(
                id = "cam17-listening-test1",
                title = "Cambridge IELTS 17 - Listening Test 1",
                summary = "Bộ đề thi thử IELTS Listening trích từ Cambridge IELTS 17",
                difficulty = "Trung bình",
                durationMinutes = 35,
                totalAudioDuration = 1815,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Buckworth Conservation Group Volunteering",
                    "Section 2: Boat Tour of Harbour & Marine Wildlife",
                    "Section 3: Course Evaluation on Child Language Acquisition",
                    "Section 4: History and Symbolism of Ancient Labyrinths"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793759097/Test1_Part1.mp3",
                userProgress = null
            )
        )
        list.add(
            ListeningTestListItemDto(
                id = "cam17-listening-test2",
                title = "Cambridge IELTS 17 - Listening Test 2",
                summary = "Bộ đề thi thử IELTS Listening trích từ Cambridge IELTS 17",
                difficulty = "Trung bình",
                durationMinutes = 35,
                totalAudioDuration = 1825,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: House Renovation and Interior Painting Quotation",
                    "Section 2: Radio Feature on Local Craft Microbreweries",
                    "Section 3: Archaeology Students Discussion on Roman Villas",
                    "Section 4: Botany Lecture on Mycorrhizal Tree Communication"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793759241/Test2_Part1.mp3",
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 16 ====================
        list.add(
            ListeningTestListItemDto(
                id = "cam16-listening-test1",
                title = "Cambridge IELTS 16 - Listening Test 1",
                summary = "Bộ đề thi thử IELTS Listening trích từ Cambridge IELTS 16",
                difficulty = "Trung bình",
                durationMinutes = 35,
                totalAudioDuration = 1820,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Holiday Apartment Rental in Green Bay",
                    "Section 2: Induction Talk for New Staff at Municipal Theatre",
                    "Section 3: Environmental Project on Desert Flora Adaptations",
                    "Section 4: Historical Development of Early Mechanical Clocks"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793759309/Test3_Part1.mp3",
                userProgress = null
            )
        )
        list.add(
            ListeningTestListItemDto(
                id = "cam16-listening-test2",
                title = "Cambridge IELTS 16 - Listening Test 2",
                summary = "Bộ đề thi thử IELTS Listening trích từ Cambridge IELTS 16",
                difficulty = "Dễ",
                durationMinutes = 35,
                totalAudioDuration = 1805,
                totalQuestions = 40,
                sectionTitles = listOf(
                    "Section 1: Culinary Cooking Class Enrollment and Schedule",
                    "Section 2: Guide to Central City Cycle Paths & Bike Sharing",
                    "Section 3: Student Review on Artificial Intelligence in Farming",
                    "Section 4: Geology Lecture on Geothermal Energy Generation"
                ),
                audioUrl = "https://res.cloudinary.com/fdawugjr/video/upload/v1793759381/Test4_Part1.mp3",
                userProgress = null
            )
        )

        return list
    }
}

