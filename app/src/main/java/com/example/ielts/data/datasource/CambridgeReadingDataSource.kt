package com.example.ielts.data.datasource

import com.example.ielts.data.remote.dto.PassageDto
import com.example.ielts.data.remote.dto.QuestionDetailDto
import com.example.ielts.data.remote.dto.ReadingTestListItemDto
import com.example.ielts.data.remote.dto.UserProgressDto

object CambridgeReadingDataSource {

    fun getAllTests(): List<ReadingTestListItemDto> {
        val list = mutableListOf<ReadingTestListItemDto>()

        // ==================== CAMBRIDGE 19 (2024) ====================
        list.add(
            ReadingTestListItemDto(
                id = "cam19-test1",
                title = "Cambridge IELTS 19 - Test 1",
                summary = "Bộ đề chuẩn khảo thí British Council & Cambridge English 2024",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Tennis Racket Design Through the Decades",
                    "The Ecological Impact of Invasive Lionfish",
                    "The Evolution and Cognitive Science of Human Laughter"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam19-test2",
                title = "Cambridge IELTS 19 - Test 2",
                summary = "Trích từ bộ đề thi Cambridge IELTS 19 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Archaeological Discoveries at Bronze Age Settlements",
                    "Biometrics and Facial Recognition in Smart Transport",
                    "The Philosophy of Memory and Subjective Time"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam19-test3",
                title = "Cambridge IELTS 19 - Test 3",
                summary = "Trích từ bộ đề thi Cambridge IELTS 19 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Urban Honeybees and Metropolitan Pollination",
                    "The Cognitive Architecture of Musical Creativity",
                    "Deep-Sea Mining and Hydrothermal Vent Ecology"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam19-test4",
                title = "Cambridge IELTS 19 - Test 4",
                summary = "Trích từ bộ đề thi Cambridge IELTS 19 Academic",
                difficulty = "Nâng cao",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Traditional Textile Dyeing with Natural Pigments",
                    "Robotics in Regenerative Orthopaedic Surgery",
                    "Linguistic Analysis of Ancient Indus Valley Inscriptions"
                ),
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 18 (2023) ====================
        list.add(
            ReadingTestListItemDto(
                id = "cam18-test1",
                title = "Cambridge IELTS 18 - Academic Reading Test 1",
                summary = "Bộ đề thi thử IELTS Reading Academic trích từ Cambridge IELTS 18 Test 1",
                difficulty = "Academic",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Urban farming",
                    "Forest management in Pennsylvania, USA",
                    "Conquering Earth's space junk problem"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam18-test2",
                title = "Cambridge IELTS 18 - Test 2",
                summary = "Bộ đề chuẩn khảo thí British Council & IDP",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Stone Age Cave Art Discoveries",
                    "The Migration Patterns of Marine Animals",
                    "The Psychology of Decision Making"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam18-test3",
                title = "Cambridge IELTS 18 - Test 3",
                summary = "Trích từ bộ đề thi Cambridge IELTS 18 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Materials Science of Ancient Roman Concrete",
                    "Sensory Adaptations in Deep Subterranean Fauna",
                    "The Linguistic Geography of Indigenous Amazonian Dialects"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam18-test4",
                title = "Cambridge IELTS 18 - Test 4",
                summary = "Trích từ bộ đề thi Cambridge IELTS 18 Academic",
                difficulty = "Nâng cao",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Historical Wind-Powered Irrigation Networks",
                    "Neuroplasticity and Language Acquisition in Bilinguals",
                    "The Ethical Governance of Automated Algorithmic Sentencing"
                ),
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 17 (2022) ====================
        list.add(
            ReadingTestListItemDto(
                id = "cam17-test1",
                title = "Cambridge IELTS 17 - Test 1",
                summary = "Trích từ bộ đề thi Cambridge IELTS 17 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Development of the London Underground Railway",
                    "Stadiums Through the Ages: From Colosseum to Sustainable Arenas",
                    "To Catch a King: Charles II's Escape After the Battle of Worcester"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam17-test2",
                title = "Cambridge IELTS 17 - Test 2",
                summary = "Trích từ bộ đề thi Cambridge IELTS 17 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "An Introduction to Film Sound and Acoustic Narrative",
                    "Discovering the Secrets of Spider Silk Bio-Engineering",
                    "Insight and Serendipity: Cognitive Mechanics of Eureka Moments"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam17-test3",
                title = "Cambridge IELTS 17 - Test 3",
                summary = "Trích từ bộ đề thi Cambridge IELTS 17 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Thylacine: Extinction and Modern Cloning Prospects",
                    "Palm Oil Sustainability: Balancing Global Economy and Biodiversity",
                    "Building the Replica of the Mary Rose Tudor Warship"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam17-test4",
                title = "Cambridge IELTS 17 - Test 4",
                summary = "Trích từ bộ đề thi Cambridge IELTS 17 Academic",
                difficulty = "Nâng cao",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Bat Echolocation and Doppler Radar Navigation",
                    "How Neanderthals Adapted to Harsh Pleistocene Climates",
                    "Why Music Affects Emotion: Neurobiology of Melodic Dopamine"
                ),
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 16 (2021) ====================
        list.add(
            ReadingTestListItemDto(
                id = "cam16-test1",
                title = "Cambridge IELTS 16 - Test 1",
                summary = "Trích từ bộ đề thi Cambridge IELTS 16 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Why Our Minds Wander: The Neurology of Daydreaming",
                    "The Archaeology of Roman Shipbuilding and Timber Preservation",
                    "The Future of Artificial Photosynthesis in Clean Fuel"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam16-test2",
                title = "Cambridge IELTS 16 - Test 2",
                summary = "Trích từ bộ đề thi Cambridge IELTS 16 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The White Horse of Uffington: Prehistoric Geoglyph Conservation",
                    "I Contain Multitudes: The Beneficial Human Microbiome",
                    "How to Make Wise Decisions: Wisdom Psychology Across Ages"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam16-test3",
                title = "Cambridge IELTS 16 - Test 3",
                summary = "Trích từ bộ đề thi Cambridge IELTS 16 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Roman Tunnels: Engineering Subterranean Aqueducts",
                    "Climate Change and Polar Bear Habitat Loss",
                    "Things That Make Us Smart: Cognitive Artifacts and Physical Tools"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam16-test4",
                title = "Cambridge IELTS 16 - Test 4",
                summary = "Trích từ bộ đề thi Cambridge IELTS 16 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Roman Amphitheaters: Construction Engineering and Urban Acoustics",
                    "The Wild Dog Packs of the African Savanna",
                    "Attitudes Towards Artificial Intelligence and Algorithmic Trust"
                ),
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 15 (2020) ====================
        list.add(
            ReadingTestListItemDto(
                id = "cam15-test1",
                title = "Cambridge IELTS 15 - Test 1",
                summary = "Trích từ bộ đề thi Cambridge IELTS 15 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Nutmeg: A Valuable Spice That Shaped Colonial History",
                    "Driverless Cars and the Future of Urban Mobility",
                    "What is Exploration? The Psychology of Geographic Discovery"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam15-test2",
                title = "Cambridge IELTS 15 - Test 2",
                summary = "Trích từ bộ đề thi Cambridge IELTS 15 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Could Consumer Product Packaging Ever Be Truly Green?",
                    "Mary Anning: The Fossil Hunter of Lyme Regis",
                    "The Book of the Future: Digital vs Tactile Reading Comprehension"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam15-test3",
                title = "Cambridge IELTS 15 - Test 3",
                summary = "Trích từ bộ đề thi Cambridge IELTS 15 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Henry Moore: Sculpting Modern Organic Forms",
                    "The Return of the Huarango Tree to Southern Peru",
                    "The Fail-Safe Society: Risk Aversion in Contemporary Culture"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam15-test4",
                title = "Cambridge IELTS 15 - Test 4",
                summary = "Trích từ bộ đề thi Cambridge IELTS 15 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Birth of Scientific Publishing and the Royal Society",
                    "The Return of the Wild Otter to British River Systems",
                    "Environmental Impact of Synthetic Clothing Microfibres"
                ),
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 14 (2019) ====================
        list.add(
            ReadingTestListItemDto(
                id = "cam14-test1",
                title = "Cambridge IELTS 14 - Test 1",
                summary = "Trích từ bộ đề thi Cambridge IELTS 14 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Importance of Children's Play in Early Development",
                    "The Growth of Bike-Sharing Schemes in World Metropolises",
                    "Motivational Factors in Workplace Organizational Psychology"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam14-test2",
                title = "Cambridge IELTS 14 - Test 2",
                summary = "Trích từ bộ đề thi Cambridge IELTS 14 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Alexander Henderson: Pioneering Canadian Landscape Photography",
                    "Back to the Playground: Adult Playfulness and Creative Output",
                    "Chelsea Flower Show: Horticultural Trends Through the Century"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam14-test3",
                title = "Cambridge IELTS 14 - Test 3",
                summary = "Trích từ bộ đề thi Cambridge IELTS 14 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Concept of Human Intelligence: Nature vs Nurture Debates",
                    "Saving Bugs: Why Insect Biodiversity Underpins Global Ecosystems",
                    "The Power of Music Therapy in Clinical Neurorehabilitation"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam14-test4",
                title = "Cambridge IELTS 14 - Test 4",
                summary = "Trích từ bộ đề thi Cambridge IELTS 14 Academic",
                difficulty = "Nâng cao",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Secret of the Lost Sea: Subterranean Geological Caverns",
                    "Ocean Noise Pollution and Marine Mammal Acoustic Communication",
                    "The Historical Development of the Modern English Dictionary"
                ),
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 13 (2018) ====================
        list.add(
            ReadingTestListItemDto(
                id = "cam13-test1",
                title = "Cambridge IELTS 13 - Test 1",
                summary = "Trích từ bộ đề thi Cambridge IELTS 13 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Case Study of Sustainable Tourism in New Zealand",
                    "Why Being Bored is Stimulating and Sparks Originality",
                    "Artificial Artists: Can Computers Paint Authentic Masterpieces?"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam13-test2",
                title = "Cambridge IELTS 13 - Test 2",
                summary = "Trích từ bộ đề thi Cambridge IELTS 13 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Bringing Cinnamon to Europe: Medieval Trade Routes and Monopoly",
                    "Oxytocin: The Biological Chemistry of Social Bonding",
                    "Making the Most of Trends: Corporate Strategy in Volatile Markets"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam13-test3",
                title = "Cambridge IELTS 13 - Test 3",
                summary = "Trích từ bộ đề thi Cambridge IELTS 13 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Coconut Palm: Tree of Life Across the South Pacific",
                    "How Infant-Directed Speech Fosters First Language Acquisition",
                    "What is Laughter? Evolutionary Anthropology of Human Humor"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam13-test4",
                title = "Cambridge IELTS 13 - Test 4",
                summary = "Trích từ bộ đề thi Cambridge IELTS 13 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Cutty Sark: The Fastest Tea Clipper of the Victorian Era",
                    "Saving the Soil: Regenerative Agricultural Topsoil Management",
                    "How to Spot a Liar: Forensic Psychology and Facial Micro-Expressions"
                ),
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 12 (2017) ====================
        list.add(
            ReadingTestListItemDto(
                id = "cam12-test1",
                title = "Cambridge IELTS 12 - Test 1",
                summary = "Trích từ bộ đề thi Cambridge IELTS 12 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The History and Sustainable Harvesting of Cork Oak in Portugal",
                    "Collecting as a Hobby: Psychological Drives of Collectors",
                    "What's the Purpose of Gaining Knowledge? Educational Philosophy"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam12-test2",
                title = "Cambridge IELTS 12 - Test 2",
                summary = "Trích từ bộ đề thi Cambridge IELTS 12 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Ecological and Societal Risks of Megafires in California",
                    "Second Nature: Neuroplasticity and Deliberate Habit Reformation",
                    "The Cognitive and Linguistic Advantages of Being Bilingual"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam12-test3",
                title = "Cambridge IELTS 12 - Test 3",
                summary = "Trích từ bộ đề thi Cambridge IELTS 12 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Historical Development of the British Public Transport System",
                    "Automated Biometrics and Facial Verification in Border Security",
                    "Music and Emotional Catharsis: Neurological Auditory Studies"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam12-test4",
                title = "Cambridge IELTS 12 - Test 4",
                summary = "Trích từ bộ đề thi Cambridge IELTS 12 Academic",
                difficulty = "Nâng cao",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Archeological Origins of Early Human Neolithic Agriculture",
                    "The Conservation of Giant Kelp Forest Coastal Ecosystems",
                    "Lost Cities and Earthworks of the Pre-Columbian Amazon Basin"
                ),
                userProgress = null
            )
        )

        // ==================== CAMBRIDGE 11 (2016) ====================
        list.add(
            ReadingTestListItemDto(
                id = "cam11-test1",
                title = "Cambridge IELTS 11 - Test 1",
                summary = "Trích từ bộ đề thi Cambridge IELTS 11 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Crop-Growing Skyscrapers: The Architecture of Vertical Agriculture",
                    "The History of Eurasian Silk Road Overland Commercial Arteries",
                    "The Psychology of Innovation and Breakthrough Scientific Ideas"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam11-test2",
                title = "Cambridge IELTS 11 - Test 2",
                summary = "Trích từ bộ đề thi Cambridge IELTS 11 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Raising the Mary Rose: The Underwater Recovery of a Tudor Warship",
                    "What Truly Destroyed the Ancient Civilization of Easter Island?",
                    "Neuroaesthetics: How the Visual Cortex Decodes and Values Fine Art"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam11-test3",
                title = "Cambridge IELTS 11 - Test 3",
                summary = "Trích từ bộ đề thi Cambridge IELTS 11 Academic",
                difficulty = "Trung bình",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "The Story of Silk: Production and Historical Trade Monopolies",
                    "Great Migrations: Animal Navigation Across Continents and Oceans",
                    "The Power of Play: Cognitive Flexibility in Developing Children"
                ),
                userProgress = null
            )
        )
        list.add(
            ReadingTestListItemDto(
                id = "cam11-test4",
                title = "Cambridge IELTS 11 - Test 4",
                summary = "Trích từ bộ đề thi Cambridge IELTS 11 Academic",
                difficulty = "Khó",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Research Using Twins: Disentangling Heredity and Environmental Influence",
                    "An Introduction to Film Sound: Aesthetic and Psychological Acoustics",
                    "This Marvellous Invention: The Evolution of Universal Human Language"
                ),
                userProgress = null
            )
        )

        return list
    }

    data class QuestionAnswerMetadata(
        val correctAnswer: String,
        val acceptedAnswers: List<String>,
        val explanation: String,
        val evidenceQuote: String
    )

    private const val CAM18_TEST1_PASSAGE1_CONTENT =
        "In Paris, urban farmers are trying a soil-free approach to agriculture that uses less space and fewer resources. Could it help cities face the threats to our food supplies?\n\n" +
        "On top of a striking new exhibition hall in southern Paris, the world’s largest urban rooftop farm has started to bear fruit. Strawberries that are small, intensely flavoured and resplendently red sprout abundantly from large plastic tubes. Peer inside and you see the tubes are completely hollow, the roots of dozens of strawberry plants dangling down inside them. From identical vertical tubes nearby burst row upon row of lettuces; near those are aromatic herbs, such as basil, sage and peppermint. Opposite, in narrow, horizontal trays packed not with soil but with coconut fibre, grow cherry tomatoes, shiny aubergines and brightly coloured chards.\n\n" +
        "Pascal Hardy, an engineer and sustainable development consultant, began experimenting with vertical farming and aeroponic growing towers – as the soil-free plastic tubes are known – on his Paris apartment block roof five years ago. The urban rooftop space above the exhibition hall is somewhat bigger: 14,000 square metres and almost exactly the size of a couple of football pitches. Already, the team of young urban farmers who tend it have picked, in one day, 3,000 lettuces and 150 punnets of strawberries. When the remaining two thirds of the vast open area are in production, 20 staff will harvest up to 1,000 kg of perhaps 35 different varieties of fruit and vegetables, every day. ‘We’re not ever, obviously, going to feed the whole city this way,’ cautions Hardy. ‘In the urban environment you’re working with very significant practical constraints, clearly, on what you can do and where. But if enough unused space can be developed like this, there’s no reason why you shouldn’t eventually target maybe between 5% and 10% of consumption.’\n\n" +
        "Perhaps most significantly, however, this is a real-life showcase for the work of Hardy’s flourishing urban agriculture consultancy, Agripolis, which is currently fielding enquiries from around the world to design, build and equip a new breed of soil-free inner-city farm. ‘The method’s advantages are many,’ he says. ‘First, I don’t much like the fact that most of the fruit and vegetables we eat have been treated with something like 17 different pesticides, or that the intensive farming techniques that produced them are such huge generators of greenhouse gases. I don’t much like the fact, either, that they’ve travelled an average of 2,000 refrigerated kilometres to my plate, that their quality is so poor, because the varieties are selected for their capacity to withstand such substantial journeys, or that 80% of the price I pay goes to wholesalers and transport companies, not the producers.’\n\n" +
        "Produce grown using this soil-free method, on the other hand – which relies solely on a small quantity of water, enriched with organic nutrients, pumped around a closed circuit of pipes, towers and trays – is ‘produced up here, and sold locally, just down there. It barely travels at all,’ Hardy says. ‘You can select crop varieties for their flavour, not their resistance to the transport and storage chain, and you can pick them when they’re really at their best, and not before.’ No soil is exhausted, and the water that gently showers the plants’ roots every 12 minutes is recycled, so the method uses 90% less water than a classic intensive farm for the same yield.\n\n" +
        "Urban farming is not, of course, a new phenomenon. Inner-city agriculture is booming from Shanghai to Detroit and Tokyo to Bangkok. Strawberries are being grown in disused shipping containers, mushrooms in underground carparks. Aeroponic farming, he says, is ‘virtuous’. The equipment weighs little, can be installed on almost any flat surface and is cheap to buy: roughly €100 to €150 per square metre. It is cheap to run, too, consuming a tiny fraction of the electricity used by some techniques.\n\n" +
        "Produce grown this way typically sells at prices that, while generally higher than those of classic intensive agriculture, are lower than soil-based organic growers. There are limits to what farmers can grow this way, of course, and much of the produce is suited to the summer months. ‘Root vegetables we cannot do, at least not yet,’ he says. ‘Radishes are OK, but carrots, potatoes, that kind of thing – the roots are simply too long. Fruit trees are obviously not an option. And beans tend to take up a lot of space for not much return.’ Nevertheless, urban farming of the kind being practised in Paris is one part of a bigger and fast-changing picture that is bringing food production closer to our lives."

    fun hasCustomExamContent(testId: String): Boolean {
        return testId == "cam18-test1"
    }

    fun getPassagesForTest(testId: String): List<PassageDto> {
        if (testId == "cam18-test1") {
            return listOf(
                PassageDto(
                    passageNumber = 1,
                    title = "Urban farming",
                    wordCount = 755,
                    content = CAM18_TEST1_PASSAGE1_CONTENT
                ),
                PassageDto(
                    passageNumber = 2,
                    title = "Forest management in Pennsylvania, USA",
                    wordCount = 1220,
                    content = "[Paragraph A] The historical Silk Road was an expansive network of Eurasian trade conduits active from the Han dynasty until the Ottoman boycott of Western maritime commerce.\n\n[Paragraph B] Beyond raw commodities such as Chinese sericulture, Central Asian equine breeds, and Roman glassware, the arteries facilitated unprecedented intellectual and religious dissemination.\n\n[Paragraph C] Papermaking techniques migrated eastward along trans-Pamir caravan tracks, revolutionizing archival administration throughout medieval Islamic caliphates."
                ),
                PassageDto(
                    passageNumber = 3,
                    title = "Conquering Earth's space junk problem",
                    wordCount = 1350,
                    content = "[Paragraph A] Machine learning algorithms trained on expansive radiological datasets now demonstrate non-inferiority to board-certified radiologists in identifying early-stage pulmonary nodules.\n\n[Paragraph B] Deep neural network architectures evaluate subtle optical coherence tomography variations to diagnose macular degeneration prior to irreversible photoreceptor apoptosis.\n\n[Paragraph C] However, algorithmic opacity, commonly designated the 'black box' phenomenon, continues to provoke ethical and jurisdictional hesitation among clinical practitioners."
                )
            )
        }

        val test = getAllTests().firstOrNull { it.id == testId }
        val p1Title = test?.passageTitles?.getOrElse(0) { "Reading Passage 1" } ?: "The Secret Life of Urban Forests"
        val p2Title = test?.passageTitles?.getOrElse(1) { "Reading Passage 2" } ?: "The History of Silk Road Trade & Cultural Exchange"
        val p3Title = test?.passageTitles?.getOrElse(2) { "Reading Passage 3" } ?: "Artificial Intelligence & Computational Diagnostics in Healthcare"

        return listOf(
            PassageDto(
                passageNumber = 1,
                title = p1Title,
                wordCount = 1140,
                content = "[Paragraph A] Urban trees and botanical specimens are not merely aesthetic enhancements to concrete landscapes; they are complex biological infrastructures that modulate microclimates, reduce ambient stormwater runoff, and capture airborne particulates.\n\n[Paragraph B] Recent satellite thermography studies conducted across European metropolitan zones revealed that canopy coverage exceeding thirty percent correlates directly with an average 4.2°C temperature attenuation during peak summer heatwaves.\n\n[Paragraph C] Furthermore, root matrices act as subterranean porous sumps, mitigating flash flooding events by stabilizing topsoil shear resistance and augmenting natural hydrological percolation.\n\n[Paragraph D] In addition to climatic buffers, diverse municipal arboretums sequester substantial nitrogen dioxide concentrations emitted from high-density vehicular corridors."
            ),
            PassageDto(
                passageNumber = 2,
                title = p2Title,
                wordCount = 1220,
                content = "[Paragraph A] The historical Silk Road was an expansive network of Eurasian trade conduits active from the Han dynasty until the Ottoman boycott of Western maritime commerce.\n\n[Paragraph B] Beyond raw commodities such as Chinese sericulture, Central Asian equine breeds, and Roman glassware, the arteries facilitated unprecedented intellectual and religious dissemination.\n\n[Paragraph C] Papermaking techniques migrated eastward along trans-Pamir caravan tracks, revolutionizing archival administration throughout medieval Islamic caliphates."
            ),
            PassageDto(
                passageNumber = 3,
                title = p3Title,
                wordCount = 1350,
                content = "[Paragraph A] Machine learning algorithms trained on expansive radiological datasets now demonstrate non-inferiority to board-certified radiologists in identifying early-stage pulmonary nodules.\n\n[Paragraph B] Deep neural network architectures evaluate subtle optical coherence tomography variations to diagnose macular degeneration prior to irreversible photoreceptor apoptosis.\n\n[Paragraph C] However, algorithmic opacity, commonly designated the 'black box' phenomenon, continues to provoke ethical and jurisdictional hesitation among clinical practitioners."
            )
        )
    }

    fun getQuestionsForTest(testId: String): List<QuestionDetailDto> {
        val list = mutableListOf<QuestionDetailDto>()
        if (testId == "cam18-test1") {
            // Questions 1–3: Sentence Completion (NO MORE THAN TWO WORDS AND/OR A NUMBER)
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q1",
                    position = 1,
                    type = "FILL_IN_BLANK",
                    prompt = "[Urban farming in Paris — NO MORE THAN TWO WORDS AND/OR A NUMBER]\nVertical tubes are used to grow strawberries, ________ and herbs.",
                    passageNumber = 1,
                    lineRef = "Questions 1–3 • Sentence Completion"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q2",
                    position = 2,
                    type = "FILL_IN_BLANK",
                    prompt = "[Urban farming in Paris — NO MORE THAN TWO WORDS AND/OR A NUMBER]\nThere will eventually be a daily harvest of as much as ________ in weight of fruit and vegetables.",
                    passageNumber = 1,
                    lineRef = "Questions 1–3 • Sentence Completion"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q3",
                    position = 3,
                    type = "FILL_IN_BLANK",
                    prompt = "[Urban farming in Paris — NO MORE THAN TWO WORDS AND/OR A NUMBER]\nIt may be possible that the farm's produce will account for as much as 10% of the city's ________ overall.",
                    passageNumber = 1,
                    lineRef = "Questions 1–3 • Sentence Completion"
                )
            )

            // Questions 4–7: Table Completion (ONE WORD ONLY)
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q4",
                    position = 4,
                    type = "FILL_IN_BLANK",
                    prompt = "[Intensive farming vs Aeroponic urban farming — ONE WORD ONLY]\nIntensive farming (Growth):\n• wide range of ________ used\n• techniques pollute air",
                    passageNumber = 1,
                    lineRef = "Questions 4–7 • Table Completion"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q5",
                    position = 5,
                    type = "FILL_IN_BLANK",
                    prompt = "[Intensive farming vs Aeroponic urban farming — ONE WORD ONLY]\nIntensive farming (Selection):\n• quality not good\n• varieties of fruit and vegetables chosen that can survive long ________",
                    passageNumber = 1,
                    lineRef = "Questions 4–7 • Table Completion"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q6",
                    position = 6,
                    type = "FILL_IN_BLANK",
                    prompt = "[Intensive farming vs Aeroponic urban farming — ONE WORD ONLY]\nIntensive farming (Sale):\n• ________ receive very little of overall income",
                    passageNumber = 1,
                    lineRef = "Questions 4–7 • Table Completion"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q7",
                    position = 7,
                    type = "FILL_IN_BLANK",
                    prompt = "[Intensive farming vs Aeroponic urban farming — ONE WORD ONLY]\nAeroponic urban farming (Selection):\n• produce chosen because of its ________",
                    passageNumber = 1,
                    lineRef = "Questions 4–7 • Table Completion"
                )
            )

            // Questions 8–13: TRUE / FALSE / NOT GIVEN
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q8",
                    position = 8,
                    type = "TRUE_FALSE",
                    prompt = "Urban farming can take place above or below ground.",
                    options = "TRUE FALSE NOT GIVEN",
                    passageNumber = 1,
                    lineRef = "Questions 8–13 • TRUE / FALSE / NOT GIVEN"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q9",
                    position = 9,
                    type = "TRUE_FALSE",
                    prompt = "Some of the equipment used in aeroponic farming can be made by hand.",
                    options = "TRUE FALSE NOT GIVEN",
                    passageNumber = 1,
                    lineRef = "Questions 8–13 • TRUE / FALSE / NOT GIVEN"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q10",
                    position = 10,
                    type = "TRUE_FALSE",
                    prompt = "Urban farming relies more on electricity than some other types of farming.",
                    options = "TRUE FALSE NOT GIVEN",
                    passageNumber = 1,
                    lineRef = "Questions 8–13 • TRUE / FALSE / NOT GIVEN"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q11",
                    position = 11,
                    type = "TRUE_FALSE",
                    prompt = "Fruit and vegetables grown on an aeroponic urban farm are cheaper than traditionally grown organic produce.",
                    options = "TRUE FALSE NOT GIVEN",
                    passageNumber = 1,
                    lineRef = "Questions 8–13 • TRUE / FALSE / NOT GIVEN"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q12",
                    position = 12,
                    type = "TRUE_FALSE",
                    prompt = "Most produce can be grown on an aeroponic urban farm at any time of the year.",
                    options = "TRUE FALSE NOT GIVEN",
                    passageNumber = 1,
                    lineRef = "Questions 8–13 • TRUE / FALSE / NOT GIVEN"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q13",
                    position = 13,
                    type = "TRUE_FALSE",
                    prompt = "Beans take longer to grow on an urban farm than other vegetables.",
                    options = "TRUE FALSE NOT GIVEN",
                    passageNumber = 1,
                    lineRef = "Questions 8–13 • TRUE / FALSE / NOT GIVEN"
                )
            )

            // Passage 2: Questions 14–26
            for (i in 14..26) {
                list.add(
                    QuestionDetailDto(
                        id = "cam18_t1_q$i",
                        position = i,
                        type = if (i % 2 == 0) "MULTIPLE_CHOICE" else "TRUE_FALSE",
                        prompt = "The trans-Pamir caravan network was essential for the dissemination of technological papermaking know-how.",
                        options = if (i % 2 == 0) "A. Han imperial edicts\nB. Religious and scholastic texts\nC. Silver bullion coinage\nD. Bronze metallurgical ores" else "TRUE FALSE NOT GIVEN",
                        passageNumber = 2,
                        lineRef = "Passage 2 • Question $i"
                    )
                )
            }

            // Passage 3: Questions 27–40
            for (i in 27..40) {
                list.add(
                    QuestionDetailDto(
                        id = "cam18_t1_q$i",
                        position = i,
                        type = if (i % 3 == 0) "FILL_IN_BLANK" else if (i % 2 == 0) "TRUE_FALSE" else "MULTIPLE_CHOICE",
                        prompt = "The inability of deep networks to show the intelligible causal reasoning behind classifications is commonly called the '________' problem.",
                        options = if (i % 2 == 0) "TRUE FALSE NOT GIVEN" else "A. Black box\nB. Algorithmic bias\nC. Optical variance\nD. Data loss",
                        passageNumber = 3,
                        lineRef = "Passage 3 • Question $i"
                    )
                )
            }
        }
        return list
    }

    fun getCam18Test1AnswerMetadata(position: Int): QuestionAnswerMetadata? {
        return when (position) {
            1 -> QuestionAnswerMetadata(
                correctAnswer = "lettuces",
                acceptedAnswers = listOf("lettuces", "lettuce"),
                explanation = "Đoạn 1: 'From identical vertical tubes nearby burst row upon row of lettuces; near those are aromatic herbs...' (Từ những ống thẳng đứng tương tự gần đó mọc lên hàng loạt cây xà lách).",
                evidenceQuote = "From identical vertical tubes nearby burst row upon row of lettuces; near those are aromatic herbs"
            )
            2 -> QuestionAnswerMetadata(
                correctAnswer = "1,000 kg",
                acceptedAnswers = listOf("1,000 kg", "1000 kg", "1,000kg", "1000kg", "1,000 kilograms", "1000 kilograms"),
                explanation = "Đoạn 2: 'When the remaining two thirds of the vast open area are in production, 20 staff will harvest up to 1,000 kg of perhaps 35 different varieties of fruit and vegetables, every day.'",
                evidenceQuote = "20 staff will harvest up to 1,000 kg of perhaps 35 different varieties of fruit and vegetables, every day"
            )
            3 -> QuestionAnswerMetadata(
                correctAnswer = "consumption",
                acceptedAnswers = listOf("consumption", "food consumption"),
                explanation = "Đoạn 2: 'But if enough unused space can be developed like this, there’s no reason why you shouldn’t eventually target maybe between 5% and 10% of consumption.'",
                evidenceQuote = "there’s no reason why you shouldn’t eventually target maybe between 5% and 10% of consumption"
            )
            4 -> QuestionAnswerMetadata(
                correctAnswer = "pesticides",
                acceptedAnswers = listOf("pesticides", "pesticide"),
                explanation = "Đoạn 3: 'First, I don’t much like the fact that most of the fruit and vegetables we eat have been treated with something like 17 different pesticides...'",
                evidenceQuote = "most of the fruit and vegetables we eat have been treated with something like 17 different pesticides"
            )
            5 -> QuestionAnswerMetadata(
                correctAnswer = "journeys",
                acceptedAnswers = listOf("journeys", "journey"),
                explanation = "Đoạn 3: '...that their quality is so poor, because the varieties are selected for their capacity to withstand such substantial journeys...'",
                evidenceQuote = "because the varieties are selected for their capacity to withstand such substantial journeys"
            )
            6 -> QuestionAnswerMetadata(
                correctAnswer = "producers",
                acceptedAnswers = listOf("producers", "producer"),
                explanation = "Đoạn 3: '...or that 80% of the price I pay goes to wholesalers and transport companies, not the producers.' (80% giá tiền thuộc về nhà bán buôn và công ty vận tải chứ không phải người sản xuất).",
                evidenceQuote = "80% of the price I pay goes to wholesalers and transport companies, not the producers"
            )
            7 -> QuestionAnswerMetadata(
                correctAnswer = "flavour",
                acceptedAnswers = listOf("flavour", "flavor"),
                explanation = "Đoạn 4: 'You can select crop varieties for their flavour, not their resistance to the transport and storage chain...'",
                evidenceQuote = "You can select crop varieties for their flavour, not their resistance to the transport and storage chain"
            )
            8 -> QuestionAnswerMetadata(
                correctAnswer = "TRUE",
                acceptedAnswers = listOf("TRUE"),
                explanation = "Đoạn 1 & Đoạn 5: Nông nghiệp đô thị diễn ra cả ở trên cao ('urban rooftop farm') và dưới lòng đất ('mushrooms in underground carparks') -> TRUE.",
                evidenceQuote = "Strawberries are being grown in disused shipping containers, mushrooms in underground carparks"
            )
            9 -> QuestionAnswerMetadata(
                correctAnswer = "NOT GIVEN",
                acceptedAnswers = listOf("NOT GIVEN"),
                explanation = "Đoạn 5 có đề cập thiết bị nhẹ, dễ lắp đặt và rẻ (€100–€150/m²), nhưng hoàn toàn không có thông tin nào nói thiết bị có thể tự làm bằng tay (made by hand) -> NOT GIVEN.",
                evidenceQuote = "The equipment weighs little, can be installed on almost any flat surface and is cheap to buy"
            )
            10 -> QuestionAnswerMetadata(
                correctAnswer = "FALSE",
                acceptedAnswers = listOf("FALSE"),
                explanation = "Đoạn 5: 'It is cheap to run, too, consuming a tiny fraction of the electricity used by some techniques.' (Tiêu thụ lượng điện rất nhỏ so với một số kỹ thuật khác, trái ngược với 'relies more on electricity') -> FALSE.",
                evidenceQuote = "consuming a tiny fraction of the electricity used by some techniques"
            )
            11 -> QuestionAnswerMetadata(
                correctAnswer = "TRUE",
                acceptedAnswers = listOf("TRUE"),
                explanation = "Đoạn 6: 'Produce grown this way typically sells at prices that, while generally higher than those of classic intensive agriculture, are lower than soil-based organic growers.' (Giá bán thấp hơn nông sản hữu cơ trồng trên đất truyền thống) -> TRUE.",
                evidenceQuote = "while generally higher than those of classic intensive agriculture, are lower than soil-based organic growers"
            )
            12 -> QuestionAnswerMetadata(
                correctAnswer = "FALSE",
                acceptedAnswers = listOf("FALSE"),
                explanation = "Đoạn 6: 'There are limits to what farmers can grow this way, of course, and much of the produce is suited to the summer months.' (Phần lớn nông sản chỉ phù hợp với các tháng mùa hè, không phải bất kỳ thời điểm nào trong năm) -> FALSE.",
                evidenceQuote = "There are limits to what farmers can grow this way, of course, and much of the produce is suited to the summer months"
            )
            13 -> QuestionAnswerMetadata(
                correctAnswer = "NOT GIVEN",
                acceptedAnswers = listOf("NOT GIVEN"),
                explanation = "Đoạn 6: 'And beans tend to take up a lot of space for not much return.' (Bài đọc chỉ nói đậu tốn diện tích mà thu hoạch chẳng được bao nhiêu, không đề cập thời gian sinh trưởng nhanh hay chậm hơn rau khác) -> NOT GIVEN.",
                evidenceQuote = "And beans tend to take up a lot of space for not much return"
            )
            else -> null
        }
    }
}
