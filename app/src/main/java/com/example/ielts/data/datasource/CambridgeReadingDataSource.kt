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
                title = "Cambridge IELTS 18 - Academic Reading Test 2",
                summary = "Bộ đề thi thử IELTS Reading Academic trích từ Cambridge IELTS 18 Test 2",
                difficulty = "Academic",
                durationMinutes = 60,
                totalQuestions = 40,
                passageTitles = listOf(
                    "Stonehenge",
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

    private const val CAM18_TEST1_PASSAGE2_CONTENT =
        "How managing low-quality wood (also known as low-use wood) for bioenergy can encourage sustainable forest management\n\n" +
        "[Paragraph A] A tree’s ‘value’ depends on several factors including its species, size, form, condition, quality, function, and accessibility, and depends on the management goals for a given forest. The same tree can be valued very differently by each person who looks at it. A large, straight black cherry tree has high value as timber to be cut into logs or made into furniture, but for a landowner more interested in wildlife habitat, the real value of that stem (or trunk) may be the food it provides to animals. Likewise, if the tree suffers from black knot disease, its value for timber decreases, but to a woodworker interested in making bowls, it brings an opportunity for a unique and beautiful piece of art.\n\n" +
        "[Paragraph B] In the past, Pennsylvania landowners were solely interested in the value of their trees as high-quality timber. The norm was to remove the stems of highest quality and leave behind poorly formed trees that were not as well suited to the site where they grew. This practice, called ‘high-grading’, has left a legacy of ‘low-use wood’ in the forests. Some people even call these ‘junk trees’, and they are abundant in Pennsylvania. These trees have lower economic value for traditional timber markets, compete for growth with higher-value trees, shade out desirable regeneration and decrease the health of a stand leaving it more vulnerable to poor weather and disease. Management that specifically targets low-use wood can help landowners manage these forest health issues, and wood energy markets help promote this.\n\n" +
        "[Paragraph C] Wood energy markets can accept less expensive wood material of lower quality than would be suitable for traditional timber markets. Most wood used for energy in Pennsylvania is used to produce heat or electricity through combustion. Many schools and hospitals use wood boiler systems to heat and power their facilities, many homes are primarily heated with wood, and some coal plants incorporate wood into their coal streams to produce electricity. Wood can also be gasified for electrical generation and can even be made into liquid fuels like ethanol and gasoline for lorries and cars. All these products are made primarily from low-use wood. Several tree- and plant-cutting approaches, which could greatly improve the long-term quality of a forest, focus strongly or solely on the use of wood for those markets.\n\n" +
        "[Paragraph D] One such approach is called a Timber Stand Improvement (TSI) Cut. In a TSI Cut, really poor-quality tree and plant material is cut down to allow more space, light, and other resources to the highest-valued stems that remain. Removing invasive plants might be another primary goal of a TSI Cut. The stems that are left behind might then grow in size and develop more foliage and larger crowns or tops that produce more coverage for wildlife; they have a better chance to regenerate in a less crowded environment. TSI Cuts can be tailored to one farmer’s specific management goals for his or her land.\n\n" +
        "[Paragraph E] Another approach that might yield a high amount of low-use wood is a Salvage Cut. With the many pests and pathogens visiting forests including hemlock wooly adelgid, Asian longhorned beetle, emerald ash borer, and gypsy moth, to name just a few, it is important to remember that those working in the forests can help ease these issues through cutting procedures. These types of cut reduce the number of sick trees and seek to manage the future spread of a pest problem. They leave vigorous trees that have stayed healthy enough to survive the outbreak.\n\n" +
        "[Paragraph F] A Shelterwood Cut, which only takes place in a mature forest that has already been thinned several times, involves removing all the mature trees when other seedlings have become established. This then allows the forester to decide which tree species are regenerated. It leaves a young forest where all trees are at a similar point in their growth. It can also be used to develop a two-tier forest so that there are two harvests and the money that comes in is spread out over a decade or more.\n\n" +
        "[Paragraph G] Thinnings and dense and dead wood removal for fire prevention also center on the production of low-use wood. However, it is important to remember that some retention of what many would classify as low-use wood is very important. The tops of trees that have been cut down should be left on the site so that their nutrients cycle back into the soil. In addition, trees with many cavities are extremely important habitats for insect predators like woodpeckers, bats and small mammals. They help control problem insects and increase the health and resilience of the forest. It is also important to remember that not all small trees are low-use. For example, many species like hawthorn provide food for wildlife. Finally, rare species of trees in a forest should also stay behind as they add to its structural diversity."

    fun hasCustomExamContent(testId: String): Boolean {
        return testId == "cam18-test1" || testId == "cam18-test2"
    }

    private const val CAM18_TEST2_PASSAGE1_CONTENT = "For centuries, historians and archaeologists have puzzled over the many mysteries of Stonehenge, a prehistoric monument that took an estimated 1,500 years to erect. Located on Salisbury Plain in southern England, it is comprised of roughly 100 massive upright stones placed in a circular layout.\n\nArchaeologists believe England's most iconic prehistoric ruin was built in several stages, with the earliest constructed 5,000 or more years ago. First, Neolithic* Britons used primitive tools, which may have been fashioned out of deer antlers, to dig a massive circular ditch and bank, or henge. Deep pits dating back to that era and located within the circle may have once held a ring of timber posts, according to some scholars.\n\nSeveral hundred years later, it is thought, Stonehenge's builders hoisted an estimated 80 bluestones, 43 of which remain today, into standing positions and placed them in either a horseshoe or circular formation. These stones have been traced all the way to the Preseli Hills in Wales, some 300 kilometres from Stonehenge. How, then, did prehistoric builders without sophisticated tools or engineering haul these boulders, which weigh up to four tons, over such a great distance?\n\nAccording to one long-standing theory among archaeologists, Stonehenge's builders fashioned sledges and rollers out of tree trunks to lug the bluestones from the Preseli Hills. They then transferred the boulders onto rafts and floated them first along the Welsh coast and then up the River Avon toward Salisbury Plain; alternatively, they may have towed each stone with a fleet of vessels. More recent archaeological hypotheses have them transporting the bluestones with supersized wicker baskets on a combination of ball bearings and long grooved planks, hauled by oxen.\n\nAs early as the 1970s, geologists have been adding their voices to the debate over how Stonehenge came into being. Challenging the classic image of industrious builders pushing, carting, rolling or hauling giant stones from faraway Wales, some scientists have suggested that it was glaciers, not humans, that carried the bluestones to Salisbury Plain. Most archaeologists have remained sceptical about this theory, however, wondering how the forces of nature could possibly have delivered the exact number of stones needed to complete the circle.\n\nThe third phase of construction took place around 2000 BCE. At this point, sandstone slabs — known as 'sarsens' — were arranged into an outer crescent or ring; some were assembled into the iconic three-pieced structures called trilithons that stand tall in the centre of Stonehenge. Some 50 of these stones are now visible on the site, which may once have contained many more. Radiocarbon dating has revealed that work continued at Stonehenge until roughly 1600 BCE, with the bluestones in particular being repositioned multiple times.\n\nBut who were the builders of Stonehenge? In the 17th century, archaeologist John Aubrey made the claim that Stonehenge was the work of druids, who had important religious, judicial and political roles in Celtic society. This theory was widely popularized by the antiquarian William Stukeley, who had unearthed primitive graves at the site. Even today, people who identify as modern druids continue to gather at Stonehenge for the summer solstice. However, in the mid-20th century, radiocarbon dating demonstrated that Stonehenge stood more than 1,000 years before the Celts inhabited the region.\n\nMany modern historians and archaeologists now agree that several distinct tribes of people contributed to Stonehenge, each undertaking a different phase of its construction. Bones, tools and other artefacts found on the site seem to support this hypothesis. The first stage was achieved by Neolithic agrarians who were likely to have been indigenous to the British Isles. Later, it is believed, groups with advanced tools and a more communal way of life left their mark on the site. Some believe that they were immigrants from the European continent, while others maintain that they were probably native Britons, descended from the original builders.\n\nIf the facts surrounding the architects and construction of Stonehenge remain shadowy at best, the purpose of the striking monument is even more of a mystery. While there is consensus among the majority of modern scholars that Stonehenge once served the function of burial ground, they have yet to determine what other purposes it had.\n\nIn the 1960s, the astronomer Gerald Hawkins suggested that the cluster of megalithic stones operated as a form of calendar, with different points corresponding to astrological phenomena such as solstices, equinoxes and eclipses occurring at different times of the year. While his theory has received a considerable amount of attention over the decades, critics maintain that Stonehenge's builders probably lacked the knowledge necessary to predict such events or that England's dense cloud cover would have obscured their view of the skies.\n\nMore recently, signs of illness and injury in the human remains unearthed at Stonehenge led a group of British archaeologists to speculate that it was considered a place of healing, perhaps because bluestones were thought to have curative powers."

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
                    wordCount = 685,
                    content = CAM18_TEST1_PASSAGE2_CONTENT
                ),
                PassageDto(
                    passageNumber = 3,
                    title = "Conquering Earth's space junk problem",
                    wordCount = 740,
                    content = "Satellites, rocket shards and collision debris are creating major traffic risks in orbit around the planet. Researchers are working to reduce these threats\n\n[Paragraph A] Last year, commercial companies, military and civil departments and amateurs sent more than 400 satellites into orbit, over four times the yearly average in the previous decade. Numbers could rise even more sharply if leading space companies follow through on plans to deploy hundreds to thousands of large constellations of satellites to space in the next few years.\n\nAll that traffic can lead to disaster. Ten years ago, a US commercial Iridium satellite smashed into an inactive Russian communications satellite called Cosmos-2251, creating thousands of new pieces of space shrapnel that now threaten other satellites in low Earth orbit — the zone stretching up to 2,000 kilometres in altitude. Altogether, there are roughly 20,000 human-made objects in orbit, from working satellites to small rocket pieces. And satellite operators can't steer away from every potential crash, because each move consumes time and fuel that could otherwise be used for the spacecraft's main job.\n\n[Paragraph B] Concern about space junk goes back to the beginning of the satellite era, but the number of objects in orbit is rising so rapidly that researchers are investigating new ways of attacking the problem. Several teams are trying to improve methods for assessing what is in orbit, so that satellite operators can work more efficiently in ever-more-crowded space. Some researchers are now starting to compile a massive data set that includes the best possible information on where everything is in orbit. Others are developing taxonomies of space debris — working on measuring properties such as the shape and size of an object, so that satellite operators know how much to worry about what's coming their way.\n\nThe alternative, many say, is unthinkable. Just a few uncontrolled space crashes could generate enough debris to set off a runaway cascade of fragments, rendering near-Earth space unusable. 'If we go on like this, we will reach a point of no return,' says Carolin Frueh, an astrodynamical researcher at Purdue University in West Lafayette, Indiana.\n\n[Paragraph C] Even as our ability to monitor space objects increases, so too does the total number of items in orbit. That means companies, governments and other players in space are collaborating in new ways to avoid a shared threat. International groups such as the Inter-Agency Space Debris Coordination Committee have developed guidelines on space sustainability. Those include inactivating satellites at the end of their useful life by venting pressurised materials or leftover fuel that might lead to explosions. The intergovernmental groups also advise lowering satellites deep enough into the atmosphere that they will burn up or disintegrate within 25 years. But so far, only about half of all missions have abided by this 25-year goal, says Holger Krag, head of the European Space Agency's space-debris office in Darmstadt, Germany. Operators of the planned large constellations of satellites say they will be responsible stewards in their enterprises in space, but Krag worries that problems could increase, despite their best intentions. 'What happens to those that fail or go bankrupt?' he asks. 'They are probably not going to spend money to remove their satellites from space.'\n\n[Paragraph D] In theory, given the vastness of space, satellite operators should have plenty of room for all these missions to fly safely without ever nearing another object. So some scientists are tackling the problem of space junk by trying to find out where all the debris is to a high degree of precision. That would alleviate the need for many of the unnecessary manoeuvres that are carried out to avoid potential collisions. 'If you knew precisely where everything was, you would almost never have a problem,' says Marlon Sorge, a space-debris specialist at the Aerospace Corporation in El Segundo, California.\n\n[Paragraph E] The field is called space traffic management, because it's similar to managing traffic on the roads or in the air. Think about a busy day at an airport, says Moriba Jah, an astrodynamicist at the University of Texas at Austin: planes line up in the sky, landing and taking off close to one another in a carefully choreographed routine. Air-traffic controllers know the location of the planes down to one metre in accuracy. The same can't be said for space debris. Not all objects in orbit are known, and even those included in databases are not tracked consistently.\n\n[Paragraph F] An additional problem is that there is no authoritative catalogue that accurately lists the orbits of all known space debris. Jah illustrates this with a web-based database that he has developed. It draws on several sources, such as catalogues maintained by the US and Russian governments, to visualise where objects are in space. When he types in an identifier for a particular space object, the database draws a purple line to designate its orbit. Only this doesn't quite work for a number of objects, such as a Russian rocket body designated in the database as object number 32280. When Jah enters that number, the database draws two purple lines: the US and Russian sources contain two completely different orbits for the same object. Jah says that it is almost impossible to tell which is correct, unless a third source of information made it possible to cross-correlate.\n\nJah describes himself as a space environmentalist: 'I want to make space a place that is safe to operate, that is free and useful for generations to come.' Until that happens, he argues, the space community will continue devolving into a tragedy in which all spaceflight operators are polluting a common resource."
                )
            )
        }

        if (testId == "cam18-test2") {
            return listOf(
                PassageDto(
                    passageNumber = 1,
                    title = "Stonehenge",
                    wordCount = 710,
                    content = CAM18_TEST2_PASSAGE1_CONTENT
                ),
                PassageDto(
                    passageNumber = 2,
                    title = "The Migration Patterns of Marine Animals",
                    wordCount = 1220,
                    content = "[Paragraph A] The historical Silk Road was an expansive network of Eurasian trade conduits active from the Han dynasty until the Ottoman boycott of Western maritime commerce.\n\n[Paragraph B] Beyond raw commodities such as Chinese sericulture, Central Asian equine breeds, and Roman glassware, the arteries facilitated unprecedented intellectual and religious dissemination.\n\n[Paragraph C] Papermaking techniques migrated eastward along trans-Pamir caravan tracks, revolutionizing archival administration throughout medieval Islamic caliphates."
                ),
                PassageDto(
                    passageNumber = 3,
                    title = "The Psychology of Decision Making",
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

            // Passage 2: Questions 14–18: Matching Information (Paragraph A–G)
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q14",
                    position = 14,
                    type = "FILL_IN_BLANK",
                    prompt = "[Which paragraph (A–G) contains the following information?]\nbad outcomes for a forest when people focus only on its financial reward",
                    passageNumber = 2,
                    lineRef = "Questions 14–18 • Matching Information (A–G)"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q15",
                    position = 15,
                    type = "FILL_IN_BLANK",
                    prompt = "[Which paragraph (A–G) contains the following information?]\nreference to the aspects of any tree that contribute to its worth",
                    passageNumber = 2,
                    lineRef = "Questions 14–18 • Matching Information (A–G)"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q16",
                    position = 16,
                    type = "FILL_IN_BLANK",
                    prompt = "[Which paragraph (A–G) contains the following information?]\nmention of the potential use of wood to help run vehicles",
                    passageNumber = 2,
                    lineRef = "Questions 14–18 • Matching Information (A–G)"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q17",
                    position = 17,
                    type = "FILL_IN_BLANK",
                    prompt = "[Which paragraph (A–G) contains the following information?]\nexamples of insects that attack trees",
                    passageNumber = 2,
                    lineRef = "Questions 14–18 • Matching Information (A–G)"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q18",
                    position = 18,
                    type = "FILL_IN_BLANK",
                    prompt = "[Which paragraph (A–G) contains the following information?]\nan alternative name for trees that produce low-use wood",
                    passageNumber = 2,
                    lineRef = "Questions 14–18 • Matching Information (A–G)"
                )
            )

            // Passage 2: Questions 19–21: Matching Classification (List of Timber Cuts: A, B or C)
            val timberCutOptions = "A. a TSI Cut\nB. a Salvage Cut\nC. a Shelterwood Cut"
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q19",
                    position = 19,
                    type = "MULTIPLE_CHOICE",
                    prompt = "[Match each purpose with the correct timber cut, A, B or C]\nto remove trees that are diseased",
                    options = timberCutOptions,
                    passageNumber = 2,
                    lineRef = "Questions 19–21 • List of Timber Cuts (A–C)"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q20",
                    position = 20,
                    type = "MULTIPLE_CHOICE",
                    prompt = "[Match each purpose with the correct timber cut, A, B or C]\nto generate income across a number of years",
                    options = timberCutOptions,
                    passageNumber = 2,
                    lineRef = "Questions 19–21 • List of Timber Cuts (A–C)"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q21",
                    position = 21,
                    type = "MULTIPLE_CHOICE",
                    prompt = "[Match each purpose with the correct timber cut, A, B or C]\nto create a forest whose trees are close in age",
                    options = timberCutOptions,
                    passageNumber = 2,
                    lineRef = "Questions 19–21 • List of Timber Cuts (A–C)"
                )
            )

            // Passage 2: Questions 22–26: Sentence Completion (ONE WORD ONLY)
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q22",
                    position = 22,
                    type = "FILL_IN_BLANK",
                    prompt = "[Complete the sentences below — ONE WORD ONLY]\nSome dead wood is removed to avoid the possibility of ________ .",
                    passageNumber = 2,
                    lineRef = "Questions 22–26 • Sentence Completion"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q23",
                    position = 23,
                    type = "FILL_IN_BLANK",
                    prompt = "[Complete the sentences below — ONE WORD ONLY]\nThe ________ from the tops of cut trees can help improve soil quality.",
                    passageNumber = 2,
                    lineRef = "Questions 22–26 • Sentence Completion"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q24",
                    position = 24,
                    type = "FILL_IN_BLANK",
                    prompt = "[Complete the sentences below — ONE WORD ONLY]\nSome damaged trees should be left, as their ________ provide habitats for a range of creatures.",
                    passageNumber = 2,
                    lineRef = "Questions 22–26 • Sentence Completion"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q25",
                    position = 25,
                    type = "FILL_IN_BLANK",
                    prompt = "[Complete the sentences below — ONE WORD ONLY]\nSome trees that are small, such as ________ , are a source of food for animals and insects.",
                    passageNumber = 2,
                    lineRef = "Questions 22–26 • Sentence Completion"
                )
            )
            list.add(
                QuestionDetailDto(
                    id = "cam18_t1_q26",
                    position = 26,
                    type = "FILL_IN_BLANK",
                    prompt = "[Complete the sentences below — ONE WORD ONLY]\nAny trees that are ________ should be left to grow, as they add to the variety of species in the forest.",
                    passageNumber = 2,
                    lineRef = "Questions 22–26 • Sentence Completion"
                )
            )

            // Passage 3: Questions 27–31: Matching Information (Section A–F)
            val p3Prompts27to31 = listOf(
                27 to "a reference to the cooperation that takes place to try and minimise risk",
                28 to "an explanation of a person’s aims",
                29 to "a description of a major collision that occurred in space",
                30 to "a comparison between tracking objects in space and the efficiency of a transportation system",
                31 to "a reference to efforts to classify space junk"
            )
            for ((pos, promptText) in p3Prompts27to31) {
                list.add(
                    QuestionDetailDto(
                        id = "cam18_t1_q$pos",
                        position = pos,
                        type = "FILL_IN_BLANK",
                        prompt = "[Which section (A–F) contains the following information?]\n$promptText",
                        passageNumber = 3,
                        lineRef = "Questions 27–31 • Matching Information (A–F)"
                    )
                )
            }

            // Passage 3: Questions 32–35: Summary Completion (ONE WORD ONLY)
            val p3Prompts32to35 = listOf(
                32 to "The committee gives advice on how the ________ of space can be achieved.",
                33 to "The committee advises that when satellites are no longer active, any unused ________ or pressurised material should be removed.",
                34 to "Unused fuel or pressurised material that could cause ________ should be removed.",
                35 to "Holger Krag points out that the operators that become ________ are unlikely to prioritise removing their satellites from space."
            )
            for ((pos, promptText) in p3Prompts32to35) {
                list.add(
                    QuestionDetailDto(
                        id = "cam18_t1_q$pos",
                        position = pos,
                        type = "FILL_IN_BLANK",
                        prompt = "[The Inter-Agency Space Debris Coordination Committee — ONE WORD ONLY]\n$promptText",
                        passageNumber = 3,
                        lineRef = "Questions 32–35 • Summary Completion"
                    )
                )
            }

            // Passage 3: Questions 36–40: Matching Statements with People (A–D)
            val peopleOptions = "A. Carolin Frueh\nB. Holger Krag\nC. Marlon Sorge\nD. Moriba Jah"
            val p3Prompts36to40 = listOf(
                36 to "Knowing the exact location of space junk would help prevent any possible danger.",
                37 to "Space should be available to everyone and should be preserved for the future.",
                38 to "A recommendation regarding satellites is widely ignored.",
                39 to "There is conflicting information about where some satellites are in space.",
                40 to "There is a risk we will not be able to undo the damage that occurs in space."
            )
            for ((pos, promptText) in p3Prompts36to40) {
                list.add(
                    QuestionDetailDto(
                        id = "cam18_t1_q$pos",
                        position = pos,
                        type = "MULTIPLE_CHOICE",
                        prompt = "[Match each statement with the correct person, A, B, C or D]\n$promptText",
                        options = peopleOptions,
                        passageNumber = 3,
                        lineRef = "Questions 36–40 • List of People (A–D)"
                    )
                )
            }
        } else if (testId == "cam18-test2") {
            // Questions 1–8: Notes Completion (NO MORE THAN TWO WORDS)
            val notesPrompts1to8 = listOf(
                1 to "Construction — Stage 1: the ditch and henge were dug, possibly using tools made from ________",
                2 to "Construction — Stage 1: ________ may have been arranged in deep pits inside the circle",
                3 to "Construction — Stage 2 (archaeological theory): builders used ________ to make sledges and rollers",
                4 to "Construction — Stage 2 (archaeological theory): ________ pulled them on giant baskets",
                5 to "Construction — Stage 2 (geological theory): they were brought from Wales by ________",
                6 to "Builders: a theory arose in the 17th century that its builders were Celtic ________",
                7 to "Purpose: many experts agree it has been used as a ________ site",
                8 to "Purpose: in the 1960s, it was suggested that it worked as a kind of ________"
            )
            for ((pos, promptText) in notesPrompts1to8) {
                list.add(
                    QuestionDetailDto(
                        id = "cam18_t2_q$pos",
                        position = pos,
                        type = "FILL_IN_BLANK",
                        prompt = "[Stonehenge Notes — Choose NO MORE THAN TWO WORDS from the passage]\n$promptText",
                        passageNumber = 1,
                        lineRef = "Questions 1–8 • Notes Completion (NO MORE THAN TWO WORDS)"
                    )
                )
            }

            // Questions 9–13: TRUE / FALSE / NOT GIVEN
            val tfngPrompts9to13 = listOf(
                9 to "During the third phase of construction, sandstone slabs were placed in both the outer areas and the middle of the Stonehenge site.",
                10 to "There is scientific proof that the bluestones stood in the same spot until approximately 1600 BCE.",
                11 to "John Aubrey's claim about Stonehenge was supported by 20th-century findings.",
                12 to "Objects discovered at Stonehenge seem to indicate that it was constructed by a number of different groups of people.",
                13 to "Criticism of Gerald Hawkins' theory about Stonehenge has come mainly from other astronomers."
            )
            for ((pos, promptText) in tfngPrompts9to13) {
                list.add(
                    QuestionDetailDto(
                        id = "cam18_t2_q$pos",
                        position = pos,
                        type = "TRUE_FALSE",
                        prompt = promptText,
                        options = listOf("TRUE", "FALSE", "NOT GIVEN"),
                        passageNumber = 1,
                        lineRef = "Questions 9–13 • TRUE / FALSE / NOT GIVEN"
                    )
                )
            }

            // Fallback questions 14–40 for Passages 2 and 3
            for (pos in 14..40) {
                val pNum = if (pos <= 26) 2 else 3
                val qType = when (pos % 3) {
                    1 -> "TRUE_FALSE"
                    2 -> "MULTIPLE_CHOICE"
                    else -> "FILL_IN_BLANK"
                }
                list.add(
                    QuestionDetailDto(
                        id = "cam18_t2_q$pos",
                        position = pos,
                        type = qType,
                        prompt = "Question $pos for Passage $pNum (Cambridge IELTS 18 - Test 2).",
                        options = if (qType == "MULTIPLE_CHOICE") listOf(
                            "A. Supporting evidence from recent studies",
                            "B. Historical background and traditional views",
                            "C. Environmental and geographical factors",
                            "D. Future implications for scientific research"
                        ) else if (qType == "TRUE_FALSE") listOf("TRUE", "FALSE", "NOT GIVEN") else null,
                        passageNumber = pNum,
                        lineRef = "Passage $pNum • Question $pos"
                    )
                )
            }
        }
        return list
    }

    fun getAnswerMetadata(testId: String, position: Int): QuestionAnswerMetadata? {
        return when (testId) {
            "cam18-test1" -> getCam18Test1AnswerMetadata(position)
            "cam18-test2" -> getCam18Test2AnswerMetadata(position)
            else -> null
        }
    }

    fun getCam18Test2AnswerMetadata(position: Int): QuestionAnswerMetadata? {
        return when (position) {
            1 -> QuestionAnswerMetadata(
                correctAnswer = "deer antlers",
                acceptedAnswers = listOf("deer antlers", "antlers"),
                explanation = "Đoạn 2: Người Anh thời Đồ đá mới đã dùng các công cụ thô sơ làm từ sừng hươu (deer antlers) để đào hào và bờ đất vòng tròn.",
                evidenceQuote = "primitive tools, which may have been fashioned out of deer antlers, to dig a massive circular ditch and bank"
            )
            2 -> QuestionAnswerMetadata(
                correctAnswer = "timber posts",
                acceptedAnswers = listOf("timber posts", "posts"),
                explanation = "Đoạn 2: Những hố sâu có niên đại từ thời kỳ đó nằm bên trong vòng tròn có thể từng giữ một vòng cột gỗ (timber posts).",
                evidenceQuote = "Deep pits dating back to that era and located within the circle may have once held a ring of timber posts"
            )
            3 -> QuestionAnswerMetadata(
                correctAnswer = "tree trunks",
                acceptedAnswers = listOf("tree trunks", "trunks"),
                explanation = "Đoạn 4: Những người xây dựng Stonehenge đã chế tạo xe trượt và con lăn từ thân cây (tree trunks) để kéo đá xanh từ đồi Preseli.",
                evidenceQuote = "builders fashioned sledges and rollers out of tree trunks to lug the bluestones from the Preseli Hills"
            )
            4 -> QuestionAnswerMetadata(
                correctAnswer = "oxen",
                acceptedAnswers = listOf("oxen"),
                explanation = "Đoạn 4: Các giả thuyết khảo cổ gần đây cho rằng họ vận chuyển đá xanh bằng những chiếc giỏ đan cỡ lớn đặt trên ván có rãnh và ổ bi, được kéo bởi bò đực (oxen).",
                evidenceQuote = "transporting the bluestones with supersized wicker baskets on a combination of ball bearings and long grooved planks, hauled by oxen"
            )
            5 -> QuestionAnswerMetadata(
                correctAnswer = "glaciers",
                acceptedAnswers = listOf("glaciers", "glacier"),
                explanation = "Đoạn 5: Một số nhà khoa học địa chất cho rằng chính các sông băng (glaciers), chứ không phải con người, đã mang đá xanh đến đồng bằng Salisbury.",
                evidenceQuote = "some scientists have suggested that it was glaciers, not humans, that carried the bluestones to Salisbury Plain"
            )
            6 -> QuestionAnswerMetadata(
                correctAnswer = "druids",
                acceptedAnswers = listOf("druids", "druid"),
                explanation = "Đoạn 7: Vào thế kỷ 17, nhà khảo cổ John Aubrey đưa ra giả thuyết rằng Stonehenge là công trình của các tu sĩ druid (druids) trong xã hội Celtic.",
                evidenceQuote = "In the 17th century, archaeologist John Aubrey made the claim that Stonehenge was the work of druids"
            )
            7 -> QuestionAnswerMetadata(
                correctAnswer = "burial",
                acceptedAnswers = listOf("burial", "burial ground"),
                explanation = "Đoạn 9: Đa số các học giả hiện đại đều đồng thuận rằng Stonehenge từng có chức năng là một nơi chôn cất (burial ground -> điền 'burial' trước từ 'site').",
                evidenceQuote = "consensus among the majority of modern scholars that Stonehenge once served the function of burial ground"
            )
            8 -> QuestionAnswerMetadata(
                correctAnswer = "calendar",
                acceptedAnswers = listOf("calendar"),
                explanation = "Đoạn 10: Vào những năm 1960, nhà thiên văn học Gerald Hawkins đề xuất rằng cụm đá cự thạch hoạt động như một dạng lịch (calendar).",
                evidenceQuote = "In the 1960s, the astronomer Gerald Hawkins suggested that the cluster of megalithic stones operated as a form of calendar"
            )
            9 -> QuestionAnswerMetadata(
                correctAnswer = "TRUE",
                acceptedAnswers = listOf("TRUE"),
                explanation = "Đoạn 6: Trong giai đoạn xây dựng thứ ba, các phiến đá sa thạch (sarsens) được xếp thành vòng ngoài (outer crescent or ring) và một số được lắp thành cấu trúc 3 khối (trilithons) ở trung tâm (in the centre of Stonehenge) -> TRUE.",
                evidenceQuote = "sandstone slabs – known as 'sarsens' – were arranged into an outer crescent or ring; some were assembled into the iconic three-pieced structures called trilithons that stand tall in the centre of Stonehenge"
            )
            10 -> QuestionAnswerMetadata(
                correctAnswer = "FALSE",
                acceptedAnswers = listOf("FALSE"),
                explanation = "Đoạn 6: Phương pháp định tuổi bằng đồng vị carbon cho thấy công việc tiếp tục đến khoảng năm 1600 TCN, đặc biệt các khối đá xanh đã bị di dời vị trí nhiều lần ('repositioned multiple times'), trái ngược với việc đứng yên một chỗ -> FALSE.",
                evidenceQuote = "with the bluestones in particular being repositioned multiple times"
            )
            11 -> QuestionAnswerMetadata(
                correctAnswer = "FALSE",
                acceptedAnswers = listOf("FALSE"),
                explanation = "Đoạn 7: Vào giữa thế kỷ 20, định tuổi bằng carbon phóng xạ đã chứng minh rằng Stonehenge có niên đại hơn 1.000 năm trước khi người Celts đến sinh sống tại vùng này (bác bỏ chứ không phải củng cố tuyên bố của John Aubrey) -> FALSE.",
                evidenceQuote = "However, in the mid-20th century, radiocarbon dating demonstrated that Stonehenge stood more than 1,000 years before the Celts inhabited the region"
            )
            12 -> QuestionAnswerMetadata(
                correctAnswer = "TRUE",
                acceptedAnswers = listOf("TRUE"),
                explanation = "Đoạn 8: Xương cốt, công cụ và các hiện vật khác được tìm thấy tại di chỉ dường như ủng hộ giả thuyết rằng nhiều bộ lạc khác nhau đã đóng góp xây dựng Stonehenge qua các giai đoạn -> TRUE.",
                evidenceQuote = "several distinct tribes of people contributed to Stonehenge, each undertaking a different phase of its construction. Bones, tools and other artefacts found on the site seem to support this hypothesis"
            )
            13 -> QuestionAnswerMetadata(
                correctAnswer = "NOT GIVEN",
                acceptedAnswers = listOf("NOT GIVEN"),
                explanation = "Đoạn 10 chỉ nói những người chỉ trích ('critics') cho rằng người xây dựng Stonehenge thiếu kiến thức dự đoán thiên văn hoặc mây dày che khuất bầu trời, không hề đề cập liệu những người chỉ trích này có chủ yếu là các nhà thiên văn học khác hay không -> NOT GIVEN.",
                evidenceQuote = "critics maintain that Stonehenge's builders probably lacked the knowledge necessary to predict such events"
            )
            else -> null
        }
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
            14 -> QuestionAnswerMetadata(
                correctAnswer = "B",
                acceptedAnswers = listOf("B", "Paragraph B"),
                explanation = "Đoạn B: Khi chủ rừng chỉ chú trọng lấy gỗ chất lượng cao ('solely interested in the value of their trees as high-quality timber'), rừng còn lại toàn cây kém chất lượng, cạnh tranh dinh dưỡng, che khuất cây con tái sinh và làm rừng dễ bị sâu bệnh, thời tiết xấu.",
                evidenceQuote = "compete for growth with higher-value trees, shade out desirable regeneration and decrease the health of a stand leaving it more vulnerable to poor weather and disease"
            )
            15 -> QuestionAnswerMetadata(
                correctAnswer = "A",
                acceptedAnswers = listOf("A", "Paragraph A"),
                explanation = "Đoạn A liệt kê các yếu tố tạo nên giá trị (worth/value) của một cái cây: 'A tree’s value depends on several factors including its species, size, form, condition, quality, function, and accessibility...'",
                evidenceQuote = "A tree’s ‘value’ depends on several factors including its species, size, form, condition, quality, function, and accessibility"
            )
            16 -> QuestionAnswerMetadata(
                correctAnswer = "C",
                acceptedAnswers = listOf("C", "Paragraph C"),
                explanation = "Đoạn C đề cập việc chế biến gỗ thành nhiên liệu lỏng chạy xe tải và ô tô: 'Wood can also be gasified for electrical generation and can even be made into liquid fuels like ethanol and gasoline for lorries and cars.'",
                evidenceQuote = "can even be made into liquid fuels like ethanol and gasoline for lorries and cars"
            )
            17 -> QuestionAnswerMetadata(
                correctAnswer = "E",
                acceptedAnswers = listOf("E", "Paragraph E"),
                explanation = "Đoạn E nêu các ví dụ về sâu hại tấn công cây rừng: 'With the many pests and pathogens visiting forests including hemlock wooly adelgid, Asian longhorned beetle, emerald ash borer, and gypsy moth...'",
                evidenceQuote = "including hemlock wooly adelgid, Asian longhorned beetle, emerald ash borer, and gypsy moth"
            )
            18 -> QuestionAnswerMetadata(
                correctAnswer = "B",
                acceptedAnswers = listOf("B", "Paragraph B"),
                explanation = "Đoạn B nêu tên gọi khác của những cây cho gỗ giá trị sử dụng thấp (low-use wood) là 'junk trees': 'This practice, called high-grading, has left a legacy of low-use wood in the forests. Some people even call these junk trees...'",
                evidenceQuote = "This practice, called ‘high-grading’, has left a legacy of ‘low-use wood’ in the forests. Some people even call these ‘junk trees’"
            )
            19 -> QuestionAnswerMetadata(
                correctAnswer = "B",
                acceptedAnswers = listOf("B"),
                explanation = "Đoạn E (Salvage Cut — B): 'These types of cut reduce the number of sick trees and seek to manage the future spread of a pest problem.' (Loại bỏ các cây bị bệnh/sâu hại).",
                evidenceQuote = "These types of cut reduce the number of sick trees and seek to manage the future spread of a pest problem"
            )
            20 -> QuestionAnswerMetadata(
                correctAnswer = "C",
                acceptedAnswers = listOf("C"),
                explanation = "Đoạn F (Shelterwood Cut — C): 'It can also be used to develop a two-tier forest so that there are two harvests and the money that comes in is spread out over a decade or more.' (Tạo thu nhập trải đều qua nhiều năm).",
                evidenceQuote = "so that there are two harvests and the money that comes in is spread out over a decade or more"
            )
            21 -> QuestionAnswerMetadata(
                correctAnswer = "C",
                acceptedAnswers = listOf("C"),
                explanation = "Đoạn F (Shelterwood Cut — C): 'It leaves a young forest where all trees are at a similar point in their growth.' (Tạo ra khu rừng trẻ có các cây ở cùng độ tuổi/giai đoạn phát triển).",
                evidenceQuote = "It leaves a young forest where all trees are at a similar point in their growth"
            )
            22 -> QuestionAnswerMetadata(
                correctAnswer = "fire",
                acceptedAnswers = listOf("fire", "fires"),
                explanation = "Đoạn G: 'Thinnings and dense and dead wood removal for fire prevention also center on the production of low-use wood.' (Loại bỏ gỗ chết để phòng chống cháy rừng -> fire).",
                evidenceQuote = "Thinnings and dense and dead wood removal for fire prevention also center on the production of low-use wood"
            )
            23 -> QuestionAnswerMetadata(
                correctAnswer = "nutrients",
                acceptedAnswers = listOf("nutrients"),
                explanation = "Đoạn G: 'The tops of trees that have been cut down should be left on the site so that their nutrients cycle back into the soil.' (Chất dinh dưỡng từ ngọn cây bị đốn hạ quay trở lại cải thiện đất -> nutrients).",
                evidenceQuote = "The tops of trees that have been cut down should be left on the site so that their nutrients cycle back into the soil"
            )
            24 -> QuestionAnswerMetadata(
                correctAnswer = "cavities",
                acceptedAnswers = listOf("cavities"),
                explanation = "Đoạn G: 'In addition, trees with many cavities are extremely important habitats for insect predators like woodpeckers, bats and small mammals.' (Các hốc cây tạo môi trường sống cho nhiều loài sinh vật -> cavities).",
                evidenceQuote = "trees with many cavities are extremely important habitats for insect predators like woodpeckers, bats and small mammals"
            )
            25 -> QuestionAnswerMetadata(
                correctAnswer = "hawthorn",
                acceptedAnswers = listOf("hawthorn"),
                explanation = "Đoạn G: 'It is also important to remember that not all small trees are low-use. For example, many species like hawthorn provide food for wildlife.' (Cây nhỏ như cây táo gai cung cấp thức ăn cho động vật hoang dã -> hawthorn).",
                evidenceQuote = "many species like hawthorn provide food for wildlife"
            )
            26 -> QuestionAnswerMetadata(
                correctAnswer = "rare",
                acceptedAnswers = listOf("rare"),
                explanation = "Đoạn G: 'Finally, rare species of trees in a forest should also stay behind as they add to its structural diversity.' (Các loài cây quý hiếm cần được giữ lại để tăng tính đa dạng cho khu rừng -> rare).",
                evidenceQuote = "Finally, rare species of trees in a forest should also stay behind as they add to its structural diversity"
            )
            27 -> QuestionAnswerMetadata(
                correctAnswer = "C",
                acceptedAnswers = listOf("C", "Section C", "Paragraph C"),
                explanation = "Đoạn C đề cập sự hợp tác giữa các công ty, chính phủ và tổ chức quốc tế để giảm thiểu rủi ro rác vũ trụ.",
                evidenceQuote = "companies, governments and other players in space are collaborating in new ways to avoid a shared threat"
            )
            28 -> QuestionAnswerMetadata(
                correctAnswer = "F",
                acceptedAnswers = listOf("F", "Section F", "Paragraph F"),
                explanation = "Đoạn F nêu mục tiêu cá nhân của Moriba Jah với tư cách là một nhà bảo vệ môi trường không gian.",
                evidenceQuote = "I want to make space a place that is safe to operate, that is free and useful for generations to come"
            )
            29 -> QuestionAnswerMetadata(
                correctAnswer = "A",
                acceptedAnswers = listOf("A", "Section A", "Paragraph A"),
                explanation = "Đoạn A mô tả vụ va chạm lớn cách đây 10 năm giữa vệ tinh thương mại Iridium của Mỹ và vệ tinh Cosmos-2251 của Nga.",
                evidenceQuote = "Ten years ago, a US commercial Iridium satellite smashed into an inactive Russian communications satellite called Cosmos-2251"
            )
            30 -> QuestionAnswerMetadata(
                correctAnswer = "E",
                acceptedAnswers = listOf("E", "Section E", "Paragraph E"),
                explanation = "Đoạn E so sánh việc quản lý giao thông vũ trụ với hệ thống điều phối máy bay tại sân bay của kiểm soát viên không lưu.",
                evidenceQuote = "The field is called space traffic management, because it's similar to managing traffic on the roads or in the air"
            )
            31 -> QuestionAnswerMetadata(
                correctAnswer = "B",
                acceptedAnswers = listOf("B", "Section B", "Paragraph B"),
                explanation = "Đoạn B đề cập nỗ lực xây dựng hệ thống phân loại (taxonomies) rác vũ trụ dựa trên hình dạng và kích thước vật thể.",
                evidenceQuote = "Others are developing taxonomies of space debris – working on measuring properties such as the shape and size of an object"
            )
            32 -> QuestionAnswerMetadata(
                correctAnswer = "sustainability",
                acceptedAnswers = listOf("sustainability"),
                explanation = "Đoạn C: Ủy ban Điều phối Mảnh vỡ Vũ trụ Liên cơ quan đã xây dựng các hướng dẫn về sự bền vững không gian (space sustainability).",
                evidenceQuote = "International groups such as the Inter-Agency Space Debris Coordination Committee have developed guidelines on space sustainability"
            )
            33 -> QuestionAnswerMetadata(
                correctAnswer = "fuel",
                acceptedAnswers = listOf("fuel"),
                explanation = "Đoạn C: Vô hiệu hóa vệ tinh cuối vòng đời bằng cách xả vật liệu có áp suất hoặc nhiên liệu thừa (leftover fuel).",
                evidenceQuote = "inactivating satellites at the end of their useful life by venting pressurised materials or leftover fuel"
            )
            34 -> QuestionAnswerMetadata(
                correctAnswer = "explosions",
                acceptedAnswers = listOf("explosions", "explosion"),
                explanation = "Đoạn C: Nhiên liệu thừa hoặc vật liệu có áp suất có thể dẫn đến các vụ nổ (lead to explosions).",
                evidenceQuote = "venting pressurised materials or leftover fuel that might lead to explosions"
            )
            35 -> QuestionAnswerMetadata(
                correctAnswer = "bankrupt",
                acceptedAnswers = listOf("bankrupt"),
                explanation = "Đoạn C: Holger Krag đặt câu hỏi điều gì xảy ra với những nhà vận hành thất bại hoặc phá sản (go bankrupt) — họ sẽ khó ưu tiên chi tiền dọn vệ tinh.",
                evidenceQuote = "What happens to those that fail or go bankrupt?"
            )
            36 -> QuestionAnswerMetadata(
                correctAnswer = "C",
                acceptedAnswers = listOf("C"),
                explanation = "Đoạn D (Marlon Sorge — C): Nếu biết chính xác vị trí của mọi mảnh vỡ với độ chính xác cao thì hầu như sẽ không bao giờ gặp sự cố va chạm.",
                evidenceQuote = "If you knew precisely where everything was, you would almost never have a problem"
            )
            37 -> QuestionAnswerMetadata(
                correctAnswer = "D",
                acceptedAnswers = listOf("D"),
                explanation = "Đoạn F (Moriba Jah — D): Mong muốn biến không gian thành nơi an toàn, tự do và hữu ích cho các thế hệ tương lai.",
                evidenceQuote = "I want to make space a place that is safe to operate, that is free and useful for generations to come"
            )
            38 -> QuestionAnswerMetadata(
                correctAnswer = "B",
                acceptedAnswers = listOf("B"),
                explanation = "Đoạn C (Holger Krag — B): Khuyến nghị hạ độ cao vệ tinh để tự bốc cháy trong vòng 25 năm mới chỉ được khoảng một nửa số sứ mệnh tuân thủ.",
                evidenceQuote = "so far, only about half of all missions have abided by this 25-year goal, says Holger Krag"
            )
            39 -> QuestionAnswerMetadata(
                correctAnswer = "D",
                acceptedAnswers = listOf("D"),
                explanation = "Đoạn F (Moriba Jah — D): Nguồn dữ liệu của Mỹ và Nga đưa ra hai quỹ đạo hoàn toàn khác nhau cho cùng một vật thể (mã 32280).",
                evidenceQuote = "the US and Russian sources contain two completely different orbits for the same object"
            )
            40 -> QuestionAnswerMetadata(
                correctAnswer = "A",
                acceptedAnswers = listOf("A"),
                explanation = "Đoạn B (Carolin Frueh — A): Cảnh báo nếu tiếp tục như hiện nay, chúng ta sẽ chạm tới điểm không thể quay đầu (a point of no return).",
                evidenceQuote = "If we go on like this, we will reach a point of no return"
            )
            else -> null
        }
    }
}
