import { PrismaClient, TestType, TestStatus, QuestionType } from "@prisma/client";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const prisma = new PrismaClient();

async function main() {
  console.log("🌱 Starting Reading seed script...");

  // Kiểm tra xem đề thi Cambridge 18 Test 1 đã tồn tại chưa
  const existingTest = await prisma.test.findFirst({
    where: {
      title: "Cambridge IELTS 18 - Academic Reading Test 1",
      type: TestType.READING,
    },
  });

  if (existingTest) {
    console.log(`ℹ️ Test already exists with ID: ${existingTest.id}. Updating questions...`);
    await prisma.test.delete({ where: { id: existingTest.id } });
  }

  const passagesData = {
    summary: "Bộ đề thi thử IELTS Reading Academic trích từ Cambridge IELTS 18 Test 1",
    difficulty: "Academic",
    totalQuestions: 40,
    durationMinutes: 60,
    passages: [
      {
        passageNumber: 1,
        title: "Urban farming",
        subtitle: "In Paris, urban farmers are trying a soil-free approach to agriculture that uses less space and fewer resources. Could it help cities face the threats to our food supplies?",
        content: `In Paris, urban farmers are trying a soil-free approach to agriculture that uses less space and fewer resources. Could it help cities face the threats to our food supplies?

On top of a striking new exhibition hall in southern Paris, the world’s largest urban rooftop farm has started to bear fruit. Strawberries that are small, intensely flavoured and resplendently red sprout abundantly from large plastic tubes. Peer inside and you see the tubes are completely hollow, the roots of dozens of strawberry plants dangling down inside them. From identical vertical tubes nearby burst row upon row of lettuces; near those are aromatic herbs, such as basil, sage and peppermint. Opposite, in narrow, horizontal trays packed not with soil but with coconut fibre, grow cherry tomatoes, shiny aubergines and brightly coloured chards.

Pascal Hardy, an engineer and sustainable development consultant, began experimenting with vertical farming and aeroponic growing towers – as the soil-free plastic tubes are known – on his Paris apartment block roof five years ago. The urban rooftop space above the exhibition hall is somewhat bigger: 14,000 square metres and almost exactly the size of a couple of football pitches. Already, the team of young urban farmers who tend it have picked, in one day, 3,000 lettuces and 150 punnets of strawberries. When the remaining two thirds of the vast open area are in production, 20 staff will harvest up to 1,000 kg of perhaps 35 different varieties of fruit and vegetables, every day. ‘We’re not ever, obviously, going to feed the whole city this way,’ cautions Hardy. ‘In the urban environment you’re working with very significant practical constraints, clearly, on what you can do and where. But if enough unused space can be developed like this, there’s no reason why you shouldn’t eventually target maybe between 5% and 10% of consumption.’

Perhaps most significantly, however, this is a real-life showcase for the work of Hardy’s flourishing urban agriculture consultancy, Agripolis, which is currently fielding enquiries from around the world to design, build and equip a new breed of soil-free inner-city farm. ‘The method’s advantages are many,’ he says. ‘First, I don’t much like the fact that most of the fruit and vegetables we eat have been treated with something like 17 different pesticides, or that the intensive farming techniques that produced them are such huge generators of greenhouse gases. I don’t much like the fact, either, that they’ve travelled an average of 2,000 refrigerated kilometres to my plate, that their quality is so poor, because the varieties are selected for their capacity to withstand such substantial journeys, or that 80% of the price I pay goes to wholesalers and transport companies, not the producers.’

Produce grown using this soil-free method, on the other hand – which relies solely on a small quantity of water, enriched with organic nutrients, pumped around a closed circuit of pipes, towers and trays – is ‘produced up here, and sold locally, just down there. It barely travels at all,’ Hardy says. ‘You can select crop varieties for their flavour, not their resistance to the transport and storage chain, and you can pick them when they’re really at their best, and not before.’ No soil is exhausted, and the water that gently showers the plants’ roots every 12 minutes is recycled, so the method uses 90% less water than a classic intensive farm for the same yield.

Urban farming is not, of course, a new phenomenon. Inner-city agriculture is booming from Shanghai to Detroit and Tokyo to Bangkok. Strawberries are being grown in disused shipping containers, mushrooms in underground carparks. Aeroponic farming, he says, is ‘virtuous’. The equipment weighs little, can be installed on almost any flat surface and is cheap to buy: roughly €100 to €150 per square metre. It is cheap to run, too, consuming a tiny fraction of the electricity used by some techniques.

Produce grown this way typically sells at prices that, while generally higher than those of classic intensive agriculture, are lower than soil-based organic growers. There are limits to what farmers can grow this way, of course, and much of the produce is suited to the summer months. ‘Root vegetables we cannot do, at least not yet,’ he says. ‘Radishes are OK, but carrots, potatoes, that kind of thing – the roots are simply too long. Fruit trees are obviously not an option. And beans tend to take up a lot of space for not much return.’ Nevertheless, urban farming of the kind being practised in Paris is one part of a bigger and fast-changing picture that is bringing food production closer to our lives.`
      },
      {
        passageNumber: 2,
        title: "Forest management in Pennsylvania, USA",
        subtitle: "How managing low-quality wood (also known as low-use wood) for bioenergy can encourage sustainable forest management",
        content: `[Paragraph A] A tree’s ‘value’ depends on several factors including its species, size, form, condition, quality, function, and accessibility, and depends on the management goals for a given forest. The same tree can be valued very differently by each person who looks at it. A large, straight black cherry tree has high value as timber to be cut into logs or made into furniture, but for a landowner more interested in wildlife habitat, the real value of that stem (or trunk) may be the food it provides to animals. Likewise, if the tree suffers from black knot disease, its value for timber decreases, but to a woodworker interested in making bowls, it brings an opportunity for a unique and beautiful piece of art.

[Paragraph B] In the past, Pennsylvania landowners were solely interested in the value of their trees as high-quality timber. The norm was to remove the stems of highest quality and leave behind poorly formed trees that were not as well suited to the site where they grew. This practice, called ‘high-grading’, has left a legacy of ‘low-use wood’ in the forests. Some people even call these ‘junk trees’, and they are abundant in Pennsylvania. These trees have lower economic value for traditional timber markets, compete for growth with higher-value trees, shade out desirable regeneration and decrease the health of a stand leaving it more vulnerable to poor weather and disease. Management that specifically targets low-use wood can help landowners manage these forest health issues, and wood energy markets help promote this.

[Paragraph C] Wood energy markets can accept less expensive wood material of lower quality than would be suitable for traditional timber markets. Most wood used for energy in Pennsylvania is used to produce heat or electricity through combustion. Many schools and hospitals use wood boiler systems to heat and power their facilities, many homes are primarily heated with wood, and some coal plants incorporate wood into their coal streams to produce electricity. Wood can also be gasified for electrical generation and can even be made into liquid fuels like ethanol and gasoline for lorries and cars. All these products are made primarily from low-use wood. Several tree- and plant-cutting approaches, which could greatly improve the long-term quality of a forest, focus strongly or solely on the use of wood for those markets.

[Paragraph D] One such approach is called a Timber Stand Improvement (TSI) Cut. In a TSI Cut, really poor-quality tree and plant material is cut down to allow more space, light, and other resources to the highest-valued stems that remain. Removing invasive plants might be another primary goal of a TSI Cut. The stems that are left behind might then grow in size and develop more foliage and larger crowns or tops that produce more coverage for wildlife; they have a better chance to regenerate in a less crowded environment. TSI Cuts can be tailored to one farmer’s specific management goals for his or her land.

[Paragraph E] Another approach that might yield a high amount of low-use wood is a Salvage Cut. With the many pests and pathogens visiting forests including hemlock wooly adelgid, Asian longhorned beetle, emerald ash borer, and gypsy moth, to name just a few, it is important to remember that those working in the forests can help ease these issues through cutting procedures. These types of cut reduce the number of sick trees and seek to manage the future spread of a pest problem. They leave vigorous trees that have stayed healthy enough to survive the outbreak.

[Paragraph F] A Shelterwood Cut, which only takes place in a mature forest that has already been thinned several times, involves removing all the mature trees when other seedlings have become established. This then allows the forester to decide which tree species are regenerated. It leaves a young forest where all trees are at a similar point in their growth. It can also be used to develop a two-tier forest so that there are two harvests and the money that comes in is spread out over a decade or more.

[Paragraph G] Thinnings and dense and dead wood removal for fire prevention also center on the production of low-use wood. However, it is important to remember that some retention of what many would classify as low-use wood is very important. The tops of trees that have been cut down should be left on the site so that their nutrients cycle back into the soil. In addition, trees with many cavities are extremely important habitats for insect predators like woodpeckers, bats and small mammals. They help control problem insects and increase the health and resilience of the forest. It is also important to remember that not all small trees are low-use. For example, many species like hawthorn provide food for wildlife. Finally, rare species of trees in a forest should also stay behind as they add to its structural diversity.`
      },
      {
        passageNumber: 3,
        title: "Artificial Intelligence in Healthcare",
        subtitle: "Revolutionizing medical diagnostics, genomics, and clinical decision-making",
        content: `A
Artificial intelligence (AI) has emerged as one of the most transformative frontiers in modern medicine. Powered by advances in machine learning, neural networks, and massive computational capacity, AI systems are demonstrating clinical diagnostic capabilities that match or even exceed seasoned human medical specialists across several specialties.

B
In medical imaging, deep learning convolutional networks have achieved unprecedented accuracy in identifying subtle abnormalities. In radiology, algorithms can detect early-stage malignant pulmonary nodules on chest CT scans with higher sensitivity than radiologists. Similarly, in ophthalmology, automated algorithms screen for diabetic retinopathy from fundus photographs with near-perfect reliability, providing rapid triage in underserved rural clinics.

C
Beyond radiology, AI algorithms are accelerating pharmaceutical discovery and molecular modeling. Traditional drug development is notorious for its grueling timeline, frequently taking over a decade and billions of dollars to bring a novel molecule to market. AI models can simulate protein folding, predict compound toxicity, and analyze chemical binding affinities in hours, slashing the exploratory phase of pre-clinical drug discovery.

D
Another vital clinical domain is predictive analytics in intensive care units (ICUs). By streaming continuous physiological telemetry—including arterial blood pressure, oxygen saturation, and electrocardiogram data—AI surveillance systems can detect incipient signs of sepsis hours before overt clinical deterioration occurs, affording clinicians invaluable time for antibiotic intervention.

E
Notwithstanding these groundbreaking breakthroughs, critical ethical, technical, and regulatory quandaries impede ubiquitous clinical deployment. Paramount among these is the 'black box' problem: deep learning networks produce classifications without revealing the intelligible causal reasoning behind their outputs, posing dilemmas for liability and informed consent.

F
Data bias represents another acute vulnerability. Most machine learning algorithms are trained on historical electronic health records gathered from affluent metropolitan populations. Consequently, algorithms may perform sub-optimally or exacerbate diagnostic disparities when deployed on underrepresented demographic cohorts.

G
Ultimately, consensus among bioethicists and clinical leaders emphasizes that AI will not replace human physicians. Rather, it will serve as an augmentative diagnostic partner, liberating clinicians from tedious clerical burdens and allowing them to dedicate more time to empathetic, personalized patient care.`
      }
    ]
  };

  const test = await prisma.test.create({
    data: {
      title: "Cambridge IELTS 18 - Academic Reading Test 1",
      description: JSON.stringify(passagesData),
      type: TestType.READING,
      status: TestStatus.PUBLISHED,
      durationMinutes: 60,
    },
  });

  console.log(`✅ Created Test: ${test.title} (ID: ${test.id})`);

  // Danh sách 40 câu hỏi chi tiết
  const questionsData = [
    // --- PASSAGE 1: Câu 1 -> 13 ---
    {
      position: 1,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Urban farming in Paris — NO MORE THAN TWO WORDS AND/OR A NUMBER]\nVertical tubes are used to grow strawberries, ________ and herbs.",
      options: null,
      answerKey: { value: "lettuces", accepted: ["lettuces", "lettuce"] },
      explanation: "Đoạn 1: 'From identical vertical tubes nearby burst row upon row of lettuces; near those are aromatic herbs...'",
    },
    {
      position: 2,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Urban farming in Paris — NO MORE THAN TWO WORDS AND/OR A NUMBER]\nThere will eventually be a daily harvest of as much as ________ in weight of fruit and vegetables.",
      options: null,
      answerKey: { value: "1,000 kg", accepted: ["1,000 kg", "1000 kg", "1,000kg", "1000kg", "1,000 kilograms", "1000 kilograms"] },
      explanation: "Đoạn 2: 'When the remaining two thirds of the vast open area are in production, 20 staff will harvest up to 1,000 kg of perhaps 35 different varieties of fruit and vegetables, every day.'",
    },
    {
      position: 3,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Urban farming in Paris — NO MORE THAN TWO WORDS AND/OR A NUMBER]\nIt may be possible that the farm's produce will account for as much as 10% of the city's ________ overall.",
      options: null,
      answerKey: { value: "consumption", accepted: ["consumption", "food consumption"] },
      explanation: "Đoạn 2: 'But if enough unused space can be developed like this, there’s no reason why you shouldn’t eventually target maybe between 5% and 10% of consumption.'",
    },
    {
      position: 4,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Intensive farming vs Aeroponic urban farming — ONE WORD ONLY]\nIntensive farming (Growth):\n• wide range of ________ used\n• techniques pollute air",
      options: null,
      answerKey: { value: "pesticides", accepted: ["pesticides", "pesticide"] },
      explanation: "Đoạn 3: 'First, I don’t much like the fact that most of the fruit and vegetables we eat have been treated with something like 17 different pesticides...'",
    },
    {
      position: 5,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Intensive farming vs Aeroponic urban farming — ONE WORD ONLY]\nIntensive farming (Selection):\n• quality not good\n• varieties of fruit and vegetables chosen that can survive long ________",
      options: null,
      answerKey: { value: "journeys", accepted: ["journeys", "journey"] },
      explanation: "Đoạn 3: '...that their quality is so poor, because the varieties are selected for their capacity to withstand such substantial journeys...'",
    },
    {
      position: 6,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Intensive farming vs Aeroponic urban farming — ONE WORD ONLY]\nIntensive farming (Sale):\n• ________ receive very little of overall income",
      options: null,
      answerKey: { value: "producers", accepted: ["producers", "producer"] },
      explanation: "Đoạn 3: '...or that 80% of the price I pay goes to wholesalers and transport companies, not the producers.'",
    },
    {
      position: 7,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Intensive farming vs Aeroponic urban farming — ONE WORD ONLY]\nAeroponic urban farming (Selection):\n• produce chosen because of its ________",
      options: null,
      answerKey: { value: "flavour", accepted: ["flavour", "flavor"] },
      explanation: "Đoạn 4: 'You can select crop varieties for their flavour, not their resistance to the transport and storage chain...'",
    },
    {
      position: 8,
      type: QuestionType.TRUE_FALSE,
      prompt: "Urban farming can take place above or below ground.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "TRUE" },
      explanation: "Đoạn 1 & Đoạn 5: Nông nghiệp đô thị diễn ra cả ở trên cao ('urban rooftop farm') và dưới lòng đất ('mushrooms in underground carparks') -> TRUE.",
    },
    {
      position: 9,
      type: QuestionType.TRUE_FALSE,
      prompt: "Some of the equipment used in aeroponic farming can be made by hand.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "NOT GIVEN" },
      explanation: "Đoạn 5 có đề cập thiết bị nhẹ, dễ lắp đặt và rẻ (€100–€150/m²), nhưng hoàn toàn không có thông tin nào nói thiết bị có thể tự làm bằng tay (made by hand) -> NOT GIVEN.",
    },
    {
      position: 10,
      type: QuestionType.TRUE_FALSE,
      prompt: "Urban farming relies more on electricity than some other types of farming.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn 5: 'It is cheap to run, too, consuming a tiny fraction of the electricity used by some techniques.' -> FALSE.",
    },
    {
      position: 11,
      type: QuestionType.TRUE_FALSE,
      prompt: "Fruit and vegetables grown on an aeroponic urban farm are cheaper than traditionally grown organic produce.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "TRUE" },
      explanation: "Đoạn 6: 'Produce grown this way typically sells at prices that, while generally higher than those of classic intensive agriculture, are lower than soil-based organic growers.' -> TRUE.",
    },
    {
      position: 12,
      type: QuestionType.TRUE_FALSE,
      prompt: "Most produce can be grown on an aeroponic urban farm at any time of the year.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn 6: 'There are limits to what farmers can grow this way, of course, and much of the produce is suited to the summer months.' -> FALSE.",
    },
    {
      position: 13,
      type: QuestionType.TRUE_FALSE,
      prompt: "Beans take longer to grow on an urban farm than other vegetables.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "NOT GIVEN" },
      explanation: "Đoạn 6: 'And beans tend to take up a lot of space for not much return.' (Không đề cập thời gian sinh trưởng lâu hơn rau khác) -> NOT GIVEN.",
    },

    // --- PASSAGE 2: Câu 14 -> 26 ---
    {
      position: 14,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Which paragraph (A–G) contains the following information?]\nbad outcomes for a forest when people focus only on its financial reward",
      options: null,
      answerKey: { value: "B", accepted: ["B", "Paragraph B"] },
      explanation: "Đoạn B: Khi chủ rừng chỉ chú trọng lấy gỗ chất lượng cao, rừng còn lại toàn cây kém chất lượng, cạnh tranh dinh dưỡng, che khuất cây con tái sinh và làm rừng dễ bị sâu bệnh, thời tiết xấu.",
    },
    {
      position: 15,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Which paragraph (A–G) contains the following information?]\nreference to the aspects of any tree that contribute to its worth",
      options: null,
      answerKey: { value: "A", accepted: ["A", "Paragraph A"] },
      explanation: "Đoạn A liệt kê các yếu tố tạo nên giá trị (worth/value) của một cái cây: 'A tree’s value depends on several factors including its species, size, form, condition, quality, function, and accessibility...'",
    },
    {
      position: 16,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Which paragraph (A–G) contains the following information?]\nmention of the potential use of wood to help run vehicles",
      options: null,
      answerKey: { value: "C", accepted: ["C", "Paragraph C"] },
      explanation: "Đoạn C đề cập việc chế biến gỗ thành nhiên liệu lỏng chạy xe tải và ô tô: 'Wood can also be gasified for electrical generation and can even be made into liquid fuels like ethanol and gasoline for lorries and cars.'",
    },
    {
      position: 17,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Which paragraph (A–G) contains the following information?]\nexamples of insects that attack trees",
      options: null,
      answerKey: { value: "E", accepted: ["E", "Paragraph E"] },
      explanation: "Đoạn E nêu các ví dụ về sâu hại tấn công cây rừng: 'With the many pests and pathogens visiting forests including hemlock wooly adelgid, Asian longhorned beetle, emerald ash borer, and gypsy moth...'",
    },
    {
      position: 18,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Which paragraph (A–G) contains the following information?]\nan alternative name for trees that produce low-use wood",
      options: null,
      answerKey: { value: "B", accepted: ["B", "Paragraph B"] },
      explanation: "Đoạn B nêu tên gọi khác của những cây cho gỗ giá trị sử dụng thấp (low-use wood) là 'junk trees'.",
    },
    {
      position: 19,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "[Match each purpose with the correct timber cut, A, B or C]\nto remove trees that are diseased",
      options: [
        "A. a TSI Cut",
        "B. a Salvage Cut",
        "C. a Shelterwood Cut"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn E (Salvage Cut — B): 'These types of cut reduce the number of sick trees and seek to manage the future spread of a pest problem.'",
    },
    {
      position: 20,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "[Match each purpose with the correct timber cut, A, B or C]\nto generate income across a number of years",
      options: [
        "A. a TSI Cut",
        "B. a Salvage Cut",
        "C. a Shelterwood Cut"
      ],
      answerKey: { value: "C" },
      explanation: "Đoạn F (Shelterwood Cut — C): 'It can also be used to develop a two-tier forest so that there are two harvests and the money that comes in is spread out over a decade or more.'",
    },
    {
      position: 21,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "[Match each purpose with the correct timber cut, A, B or C]\nto create a forest whose trees are close in age",
      options: [
        "A. a TSI Cut",
        "B. a Salvage Cut",
        "C. a Shelterwood Cut"
      ],
      answerKey: { value: "C" },
      explanation: "Đoạn F (Shelterwood Cut — C): 'It leaves a young forest where all trees are at a similar point in their growth.'",
    },
    {
      position: 22,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Complete the sentences below — ONE WORD ONLY]\nSome dead wood is removed to avoid the possibility of ________ .",
      options: null,
      answerKey: { value: "fire", accepted: ["fire", "fires"] },
      explanation: "Đoạn G: 'Thinnings and dense and dead wood removal for fire prevention also center on the production of low-use wood.'",
    },
    {
      position: 23,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Complete the sentences below — ONE WORD ONLY]\nThe ________ from the tops of cut trees can help improve soil quality.",
      options: null,
      answerKey: { value: "nutrients", accepted: ["nutrients"] },
      explanation: "Đoạn G: 'The tops of trees that have been cut down should be left on the site so that their nutrients cycle back into the soil.'",
    },
    {
      position: 24,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Complete the sentences below — ONE WORD ONLY]\nSome damaged trees should be left, as their ________ provide habitats for a range of creatures.",
      options: null,
      answerKey: { value: "cavities", accepted: ["cavities"] },
      explanation: "Đoạn G: 'In addition, trees with many cavities are extremely important habitats for insect predators like woodpeckers, bats and small mammals.'",
    },
    {
      position: 25,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Complete the sentences below — ONE WORD ONLY]\nSome trees that are small, such as ________ , are a source of food for animals and insects.",
      options: null,
      answerKey: { value: "hawthorn", accepted: ["hawthorn"] },
      explanation: "Đoạn G: 'For example, many species like hawthorn provide food for wildlife.'",
    },
    {
      position: 26,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "[Complete the sentences below — ONE WORD ONLY]\nAny trees that are ________ should be left to grow, as they add to the variety of species in the forest.",
      options: null,
      answerKey: { value: "rare", accepted: ["rare"] },
      explanation: "Đoạn G: 'Finally, rare species of trees in a forest should also stay behind as they add to its structural diversity.'",
    },

    // --- PASSAGE 3: Câu 27 -> 40 ---
    {
      position: 27,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "In medical imaging, what technology has allowed AI to match human diagnostic accuracy?",
      options: [
        "A. Manual data entry by clinical clerks",
        "B. Convolutional neural networks and deep learning algorithms",
        "C. Standardized blood chemistry centrifuges",
        "D. Mechanical analog computing relays"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn B: 'In medical imaging, deep learning convolutional networks have achieved unprecedented accuracy in identifying subtle abnormalities.'",
    },
    {
      position: 28,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "In which ophthalmological application has automated AI shown near-perfect screening reliability?",
      options: [
        "A. Glaucoma laser surgery planning",
        "B. Diabetic retinopathy screening from fundus photography",
        "C. Corneal transplant rejection modeling",
        "D. Color blindness corrective lens design"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn B: 'automated algorithms screen for diabetic retinopathy from fundus photographs with near-perfect reliability.'",
    },
    {
      position: 29,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "How does AI transform the initial phase of pharmaceutical development according to Paragraph C?",
      options: [
        "A. By guaranteeing zero side-effects during human clinical trials",
        "B. By simulating protein folding and predicting compound toxicity in hours",
        "C. By replacing all laboratory synthesis with synthetic vitamin supplements",
        "D. By eliminating the necessity of regulatory approval from the FDA"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn C: 'AI models can simulate protein folding, predict compound toxicity, and analyze chemical binding affinities in hours...'",
    },
    {
      position: 30,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "In intensive care units (ICUs), what critical condition can AI surveillance detect hours prior to overt deterioration?",
      options: [
        "A. Type 1 diabetes onset",
        "B. Sepsis",
        "C. Congenital heart defects",
        "D. Chronic osteoporosis"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn D: 'AI surveillance systems can detect incipient signs of sepsis hours before overt clinical deterioration occurs...'",
    },
    {
      position: 31,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "What is the 'black box' problem referenced in Paragraph E?",
      options: [
        "A. High cost of physical server racks in hospital basements",
        "B. Inability of deep networks to show the intelligible causal reasoning behind classifications",
        "C. The physical failure of digital hard drives during power outages",
        "D. The complete loss of patient privacy over wireless hospital networks"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn E: 'deep learning networks produce classifications without revealing the intelligible causal reasoning behind their outputs...'",
    },
    {
      position: 32,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "Why does data bias occur in medical AI models according to Paragraph F?",
      options: [
        "A. Because algorithms are trained predominantly on electronic health records from affluent metropolitan populations",
        "B. Because medical students refuse to input patient diagnostic notes",
        "C. Because pharmaceutical companies alter digital biopsy scans",
        "D. Because hospitals only purchase foreign computer software"
      ],
      answerKey: { value: "A" },
      explanation: "Đoạn F: 'Most machine learning algorithms are trained on historical electronic health records gathered from affluent metropolitan populations.'",
    },
    {
      position: 33,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "What is the consensus among medical leaders regarding the future role of AI in medicine (Paragraph G)?",
      options: [
        "A. It will completely replace human doctors within five years",
        "B. It should be banned from all clinical decision-making",
        "C. It will serve as an augmentative diagnostic partner assisting human physicians",
        "D. It will exclusively perform administrative accounting tasks"
      ],
      answerKey: { value: "C" },
      explanation: "Đoạn G: 'consensus among bioethicists and clinical leaders emphasizes that AI will not replace human physicians. Rather, it will serve as an augmentative diagnostic partner...'",
    },
    {
      position: 34,
      type: QuestionType.TRUE_FALSE,
      prompt: "AI algorithms can detect malignant lung nodules on chest CT scans with higher sensitivity than radiologists.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "TRUE" },
      explanation: "Đoạn B: 'algorithms can detect early-stage malignant pulmonary nodules on chest CT scans with higher sensitivity than radiologists.'",
    },
    {
      position: 35,
      type: QuestionType.TRUE_FALSE,
      prompt: "Traditional drug development typically takes under twelve months and costs less than one million dollars.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn C: 'frequently taking over a decade and billions of dollars to bring a novel molecule to market.'",
    },
    {
      position: 36,
      type: QuestionType.TRUE_FALSE,
      prompt: "AI algorithms can assist triage in remote clinics where eye specialists are scarce.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "TRUE" },
      explanation: "Đoạn B: 'providing rapid triage in underserved rural clinics.'",
    },
    {
      position: 37,
      type: QuestionType.TRUE_FALSE,
      prompt: "Algorithms trained on biased metropolitan data always perform equally well across all minority cohorts.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn F: 'algorithms may perform sub-optimally or exacerbate diagnostic disparities when deployed on underrepresented demographic cohorts.'",
    },
    {
      position: 38,
      type: QuestionType.TRUE_FALSE,
      prompt: "European medical authorities have officially mandated that all surgeries must be performed solely by AI robots.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "NOT GIVEN" },
      explanation: "Không có thông tin nào trong bài nói về quy định bắt buộc phải phẫu thuật hoàn toàn bằng robot AI.",
    },
    {
      position: 39,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "The inability to explain the reasoning of deep neural networks is commonly known as the '________' problem.",
      options: null,
      answerKey: { value: "black box", accepted: ["black box", "black-box"] },
      explanation: "Đoạn E: 'Paramount among these is the black box problem: deep learning networks produce classifications without revealing the intelligible causal reasoning...'",
    },
    {
      position: 40,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "Medical experts believe AI will liberate healthcare workers from burdensome ________ tasks, enabling more patient-centered care.",
      options: null,
      answerKey: { value: "clerical", accepted: ["clerical", "administrative"] },
      explanation: "Đoạn G: 'liberating clinicians from tedious clerical burdens and allowing them to dedicate more time to empathetic, personalized patient care.'",
    },
  ];

  for (const q of questionsData) {
    await prisma.question.create({
      data: {
        testId: test.id,
        position: q.position,
        type: q.type,
        prompt: q.prompt,
        options: q.options ? q.options : undefined,
        answerKey: q.answerKey,
        explanation: q.explanation,
        points: 1,
      },
    });
  }

  console.log(`🎉 Successfully seeded 40 questions for Test: ${test.title}!`);

  // --- SEED LISTENING TEST ---
  console.log("🌱 Starting Listening seed script...");
  const listeningDataPath = path.resolve(__dirname, "../src/data/listening_cambridge18_test1.json");
  if (fs.existsSync(listeningDataPath)) {
    const rawData = fs.readFileSync(listeningDataPath, "utf-8");
    const listeningData = JSON.parse(rawData);

    const existingListening = await prisma.test.findFirst({
      where: {
        title: listeningData.title,
        type: TestType.LISTENING,
      },
    });

    if (existingListening) {
      console.log(`ℹ️ Listening test already exists with ID: ${existingListening.id}. Re-creating...`);
      await prisma.test.delete({ where: { id: existingListening.id } });
    }

    const descriptionPayload = JSON.stringify({
      summary: listeningData.summary,
      difficulty: listeningData.difficulty,
      durationMinutes: listeningData.durationMinutes,
      totalAudioDuration: listeningData.totalAudioDuration,
      audioUrl: listeningData.audioUrl,
      sections: listeningData.sections,
    });

    const listeningTest = await prisma.test.create({
      data: {
        title: listeningData.title,
        description: descriptionPayload,
        type: TestType.LISTENING,
        status: TestStatus.PUBLISHED,
        durationMinutes: listeningData.durationMinutes ?? 35,
      },
    });

    for (const q of listeningData.questions) {
      await prisma.question.create({
        data: {
          testId: listeningTest.id,
          position: q.position,
          type: q.type === "FILL_IN_BLANK" ? QuestionType.FILL_IN_BLANK : QuestionType.MULTIPLE_CHOICE,
          prompt: q.prompt,
          options: q.options ? q.options : undefined,
          answerKey: q.answerKey,
          explanation: q.explanation,
          points: 1,
        },
      });
    }

    console.log(`🎉 Successfully seeded 40 questions for Listening Test: ${listeningTest.title}!`);
  }

  // Seed additional tests from catalog
  const catalogPath = path.resolve(__dirname, "../src/data/listening_tests_catalog.json");
  if (fs.existsSync(catalogPath)) {
    const rawCatalog = fs.readFileSync(catalogPath, "utf-8");
    const catalogTests = JSON.parse(rawCatalog) as Array<{
      title: string;
      summary: string;
      difficulty: string;
      durationMinutes: number;
      totalAudioDuration: number;
      audioUrl: string;
      sections: Array<{ sectionNumber: number; title: string; audioUrl: string; durationSeconds: number }>;
    }>;

    for (const item of catalogTests) {
      const existing = await prisma.test.findFirst({
        where: { title: item.title, type: TestType.LISTENING },
      });
      if (existing) {
        await prisma.test.delete({ where: { id: existing.id } });
      }

      const descPayload = JSON.stringify({
        summary: item.summary,
        difficulty: item.difficulty,
        durationMinutes: item.durationMinutes,
        totalAudioDuration: item.totalAudioDuration,
        audioUrl: item.audioUrl,
        sections: item.sections,
      });

      const newTest = await prisma.test.create({
        data: {
          title: item.title,
          description: descPayload,
          type: TestType.LISTENING,
          status: TestStatus.PUBLISHED,
          durationMinutes: item.durationMinutes ?? 35,
        },
      });

      // Tạo 40 câu hỏi mẫu chuẩn cho đề
      const questionsToInsert = [];
      for (let i = 1; i <= 40; i++) {
        let qType = QuestionType.MULTIPLE_CHOICE;
        let prompt = `Question ${i}: Choose the correct letter A, B, or C`;
        let options = ["A. Option A", "B. Option B", "C. Option C"];
        let answerKey: Record<string, unknown> = { value: "A", accepted: ["A"] };
        let sectionNumber = 1;

        if (i <= 10) {
          sectionNumber = 1;
          qType = QuestionType.FILL_IN_BLANK;
          prompt = `Question ${i}: Complete the notes below with ONE WORD AND/OR A NUMBER: Detail ________`;
          options = [];
          answerKey = { value: "center", accepted: ["center", "centre", "central"] };
        } else if (i <= 20) {
          sectionNumber = 2;
          qType = QuestionType.MULTIPLE_CHOICE;
          prompt = `Question ${i}: What is highlighted about the community facility?`;
          options = ["A. It opened last month", "B. Free for residents", "C. Requires booking in advance"];
          answerKey = { value: "B", accepted: ["B"] };
        } else if (i <= 30) {
          sectionNumber = 3;
          qType = QuestionType.MULTIPLE_CHOICE;
          prompt = `Question ${i}: What surprised the students most during their project research?`;
          options = ["A. The high cost involved", "B. The speed of implementation", "C. The lack of previous data"];
          answerKey = { value: "C", accepted: ["C"] };
        } else {
          sectionNumber = 4;
          qType = QuestionType.FILL_IN_BLANK;
          prompt = `Question ${i}: The speaker concludes that effective ________ is vital for future sustainability.`;
          options = [];
          answerKey = { value: "management", accepted: ["management", "planning"] };
        }

        const expObj = {
          sectionNumber,
          timestampStart: (sectionNumber - 1) * 420 + ((i % 10) + 1) * 35,
          quote: `Audio transcript snippet corresponding to answer for Question ${i}.`,
          distractor: `Explanation and distractor analysis for Question ${i}.`,
        };

        questionsToInsert.push({
          testId: newTest.id,
          position: i,
          type: qType,
          prompt,
          options: options.length > 0 ? options : undefined,
          answerKey,
          explanation: JSON.stringify(expObj),
          points: 1,
        });
      }

      await prisma.question.createMany({
        data: questionsToInsert,
      });

      console.log(`✅ Seeded catalog test: ${item.title} with 40 questions!`);
    }
  }
}

main()
  .catch((e) => {
    console.error("❌ Error seeding database:", e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
