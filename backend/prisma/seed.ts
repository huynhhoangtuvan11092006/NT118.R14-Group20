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
        title: "Urban Farming",
        subtitle: "Growing crops in vertical and urban spaces",
        content: `A
Vertical farming is the practice of producing food in vertically stacked layers, vertically inclined surfaces and/or integrated into other structures (such as in a skyscraper, used warehouse, or shipping container). The modern ideas of vertical farming use indoor farming techniques and controlled-environment agriculture (CEA) technology, where all environmental factors can be controlled.

B
These facilities utilize artificial control of light, environmental control (humidity, temperature, gases...) and fertigation. Some vertical farms use techniques similar to greenhouses, where natural sunlight can be augmented with artificial lighting and metal reflectors. In contrast to conventional farming, vertical farming does not require soil. Instead, methods such as hydroponics, aquaponics, or aeroponics are used.

C
Proponents point to several advantages of vertical farming. Chief among these is the dramatic reduction in the amount of land required. Because crops are stacked vertically, a vertical farm can produce as much as ten times the yield of an equivalent area of horizontal farmland. Furthermore, indoor farming eliminates the threat of adverse weather conditions, floods, droughts, and pests, enabling year-round harvest cycles.

D
Another significant benefit is water conservation. Traditional agricultural irrigation accounts for roughly 70 percent of global freshwater consumption. In closed-loop vertical systems, water is continuously recycled and transpired moisture is collected and reused, cutting water usage by up to 95 percent compared to open-field farming.

E
However, vertical farming faces substantial challenges. The primary obstacle is the colossal upfront capital cost required to construct high-tech facilities equipped with sensors, automated robotics, and LED arrays. Additionally, the electricity demanded to power 24-hour artificial lighting can lead to higher operational costs and a significant carbon footprint unless clean, renewable energy sources are employed.

F
Despite these hurdles, urban planners and food security experts believe vertical farming will play an indispensable role in feeding the projected global population of nearly 10 billion by 2050, especially as urbanization accelerates and arable land becomes increasingly scarce.`
      },
      {
        passageNumber: 2,
        title: "The Forest Canopy",
        subtitle: "Exploring the secret life in the treetops of tropical rainforests",
        content: `A
The forest canopy, the upper layer formed by mature tree crowns, contains an extraordinary proportion of the world's terrestrial biodiversity. Historically, this treetop realm was virtually inaccessible to scientists, earning it the moniker 'the eighth continent' due to how little was known about its complex ecological web.

B
Solar radiation is captured predominantly in the canopy, where leaves intercept over 80 percent of incoming sunlight. This abundant energy fuels rapid photosynthesis, creating lush micro-habitats that host thousands of species of epiphytes (air plants), insects, birds, and arboreal mammals that never touch the ground throughout their lifespans.

C
In recent decades, pioneering canopy biologists invented new technologies to explore this elevated ecosystem. Early rope-climbing methods borrowed from mountaineering were gradually replaced by canopy walkways, hot-air balloons, and construction cranes installed in tropical reserves in Panama and Australia, allowing researchers to study treetops in three dimensions.

D
One crucial discovery is the role of canopy epiphytes, such as bromeliads and orchids, in nutrient cycling. These plants collect falling organic debris and rainwater in their leaf bases, forming 'suspended soils' high above the earth. These aerial compost heaps harbor specialized invertebrates and provide critical moisture reservoirs during prolonged dry spells.

E
Canopy environments also act as vital atmospheric regulators. By absorbing immense amounts of carbon dioxide and releasing moisture through evapotranspiration, the rainforest canopy drives regional weather patterns and exerts a cooling effect on the global climate.

F
Nevertheless, canopy habitats are exceptionally vulnerable to anthropogenic disturbances. Selective logging removes the tallest emergent trees, fracturing the canopy framework and drastically altering light and humidity levels beneath, causing cascading extinctions among specialized canopy organisms.`
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
      type: QuestionType.TRUE_FALSE,
      prompt: "Vertical farming requires more land area than conventional open-field agriculture.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn C khẳng định: Vertical farming giúp giảm mạnh diện tích đất, một trang trại thẳng đứng có thể cho sản lượng gấp 10 lần diện tích nông nghiệp truyền thống tương đương.",
    },
    {
      position: 2,
      type: QuestionType.TRUE_FALSE,
      prompt: "All vertical farms rely exclusively on natural sunlight to grow crops.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn B chỉ ra rằng các trang trại này kiểm soát nhân tạo ánh sáng (artificial light) và chỉ bổ sung ánh sáng tự nhiên chứ không phụ thuộc độc quyền vào mặt trời.",
    },
    {
      position: 3,
      type: QuestionType.TRUE_FALSE,
      prompt: "Adverse weather events such as floods do not disrupt indoor vertical farming harvests.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "TRUE" },
      explanation: "Đoạn C nêu rõ: Canh tác trong nhà loại bỏ hoàn toàn các mối đe dọa từ thời tiết bất lợi, lũ lụt, hạn hán và sâu bệnh, cho phép thu hoạch quanh năm.",
    },
    {
      position: 4,
      type: QuestionType.TRUE_FALSE,
      prompt: "Closed-loop vertical farming systems can decrease agricultural water usage by up to 95 percent.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "TRUE" },
      explanation: "Đoạn D nêu rõ: Hệ thống tuần hoàn khép kín tái sử dụng nước liên tục, giúp cắt giảm lượng nước tiêu thụ tới 95% so với đồng ruộng truyền thống.",
    },
    {
      position: 5,
      type: QuestionType.TRUE_FALSE,
      prompt: "Vertical farms are currently cheaper to build and operate than traditional greenhouses.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn E nhấn mạnh trở ngại hàng đầu là chi phí đầu tư ban đầu khổng lồ (colossal upfront capital cost) và tiền điện cao do đèn LED 24/7.",
    },
    {
      position: 6,
      type: QuestionType.TRUE_FALSE,
      prompt: "Vertical farming produces zero carbon emissions regardless of the electricity source used.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn E ghi nhận lượng điện tiêu thụ lớn sẽ tạo ra dấu chân carbon đáng kể (significant carbon footprint) trừ khi sử dụng năng lượng tái tạo.",
    },
    {
      position: 7,
      type: QuestionType.TRUE_FALSE,
      prompt: "The United Nations has officially mandated vertical farming in all developing countries.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "NOT GIVEN" },
      explanation: "Đoạn F chỉ đề cập các chuyên gia tin rằng vertical farming sẽ đóng vai trò quan trọng, không có thông tin Liên Hợp Quốc bắt buộc triển khai.",
    },
    {
      position: 8,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "Instead of soil, vertical farms often cultivate plants using methods such as aeroponics or ________.",
      options: null,
      answerKey: { value: "hydroponics", accepted: ["hydroponics", "aquaponics"] },
      explanation: "Đoạn B: 'In contrast to conventional farming, vertical farming does not require soil. Instead, methods such as hydroponics, aquaponics, or aeroponics are used.'",
    },
    {
      position: 9,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "Traditional agricultural irrigation accounts for roughly ________ percent of global freshwater consumption.",
      options: null,
      answerKey: { value: "70", accepted: ["70", "70%", "70 percent"] },
      explanation: "Đoạn D: 'Traditional agricultural irrigation accounts for roughly 70 percent of global freshwater consumption.'",
    },
    {
      position: 10,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "In vertical farms, the high consumption of ________ can substantially increase operational expenditures.",
      options: null,
      answerKey: { value: "electricity", accepted: ["electricity", "power", "energy"] },
      explanation: "Đoạn E: 'Additionally, the electricity demanded to power 24-hour artificial lighting can lead to higher operational costs...'",
    },
    {
      position: 11,
      type: QuestionType.FILL_IN_BLANK,
      prompt: "Food security experts project that the worldwide population will reach nearly ________ billion by the year 2050.",
      options: null,
      answerKey: { value: "10", accepted: ["10", "ten", "10 billion"] },
      explanation: "Đoạn F: 'feeding the projected global population of nearly 10 billion by 2050.'",
    },
    {
      position: 12,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "According to Paragraph B, what is a key feature of Controlled-Environment Agriculture (CEA)?",
      options: [
        "A. Complete dependence on natural seasonal rainfall",
        "B. Artificial control of light, humidity, and temperature",
        "C. Mandatory use of organic animal compost",
        "D. Open-air field cultivation without glass enclosures"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn B chỉ rõ: CEA cho phép kiểm soát nhân tạo tất cả các yếu tố môi trường như ánh sáng, độ ẩm, nhiệt độ và dinh dưỡng.",
    },
    {
      position: 13,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "What is the primary factor limiting the immediate widespread adoption of vertical farms according to Paragraph E?",
      options: [
        "A. Complete lack of consumer interest in indoor produce",
        "B. Shortage of agricultural computer software",
        "C. Substantial upfront construction and technology costs",
        "D. Inability to produce edible leafy greens"
      ],
      answerKey: { value: "C" },
      explanation: "Đoạn E nêu rõ: 'The primary obstacle is the colossal upfront capital cost required to construct high-tech facilities...'",
    },

    // --- PASSAGE 2: Câu 14 -> 26 ---
    {
      position: 14,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "Why was the forest canopy historically described as 'the eighth continent'?",
      options: [
        "A. Because of its vast geographic area spanning oceans",
        "B. Due to extreme difficulty of access and lack of scientific knowledge",
        "C. Because it is inhabited exclusively by endangered birds",
        "D. Due to its discovery by eighteenth-century explorers"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn A: 'Historically, this treetop realm was virtually inaccessible to scientists, earning it the moniker 'the eighth continent' due to how little was known...'",
    },
    {
      position: 15,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "According to Paragraph B, approximately how much sunlight is intercepted by the upper leaves of the canopy?",
      options: [
        "A. Less than 20 percent",
        "B. Exactly 50 percent",
        "C. Over 80 percent",
        "D. Almost 100 percent in all weather"
      ],
      answerKey: { value: "C" },
      explanation: "Đoạn B: 'Solar radiation is captured predominantly in the canopy, where leaves intercept over 80 percent of incoming sunlight.'",
    },
    {
      position: 16,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "Which of the following devices was NOT mentioned as a tool used to explore the canopy in modern research?",
      options: [
        "A. Industrial construction cranes",
        "B. Canopy walkways",
        "C. Submersible research vehicles",
        "D. Hot-air balloons"
      ],
      answerKey: { value: "C" },
      explanation: "Đoạn C liệt kê canopy walkways, hot-air balloons, construction cranes. Tàu ngầm (submersible) không hề được dùng trong rừng.",
    },
    {
      position: 17,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "What are 'suspended soils' formed by epiphytic plants according to Paragraph D?",
      options: [
        "A. Artificial fertilizers introduced by field researchers",
        "B. Collections of falling organic debris and trapped rainwater",
        "C. Compacted clay transported from the ground by ants",
        "D. Mineral dust blown from desert plains"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn D: 'These plants collect falling organic debris and rainwater in their leaf bases, forming suspended soils high above the earth.'",
    },
    {
      position: 18,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "How does the rainforest canopy influence regional weather patterns according to Paragraph E?",
      options: [
        "A. By absorbing carbon dioxide and releasing moisture through evapotranspiration",
        "B. By preventing clouds from forming over agricultural fields",
        "C. By trapping greenhouse gases permanently under tree bark",
        "D. By generating geothermal heat from deep subterranean roots"
      ],
      answerKey: { value: "A" },
      explanation: "Đoạn E: 'By absorbing immense amounts of carbon dioxide and releasing moisture through evapotranspiration, the rainforest canopy drives regional weather patterns...'",
    },
    {
      position: 19,
      type: QuestionType.MULTIPLE_CHOICE,
      prompt: "What is the primary danger posed by selective logging in the forest canopy (Paragraph F)?",
      options: [
        "A. Increased risk of underground volcanic activity",
        "B. Destruction of tallest emergent trees, disrupting light and moisture levels",
        "C. Total contamination of groundwater with industrial pesticides",
        "D. Accelerated spread of bacterial infections among primates"
      ],
      answerKey: { value: "B" },
      explanation: "Đoạn F: 'Selective logging removes the tallest emergent trees, fracturing the canopy framework and drastically altering light and humidity levels beneath...'",
    },
    {
      position: 20,
      type: QuestionType.TRUE_FALSE,
      prompt: "Many organisms living in the tropical forest canopy spend their entire lives without descending to the forest floor.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "TRUE" },
      explanation: "Đoạn B: 'epiphytes (air plants), insects, birds, and arboreal mammals that never touch the ground throughout their lifespans.'",
    },
    {
      position: 21,
      type: QuestionType.TRUE_FALSE,
      prompt: "Canopy construction cranes were first deployed exclusively in European coniferous forests.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn C ghi rõ cần cẩu được lắp đặt tại các khu bảo tồn rừng nhiệt đới ở Panama và Australia.",
    },
    {
      position: 22,
      type: QuestionType.TRUE_FALSE,
      prompt: "Bromeliads obtain water only from underground taproots extending into deep soil.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "FALSE" },
      explanation: "Đoạn D: Bromeliads là thực vật phụ sinh (epiphytes) tích trữ nước mưa trực tiếp ở bẹ lá trên ngọn cây, không cắm rễ xuống đất.",
    },
    {
      position: 23,
      type: QuestionType.TRUE_FALSE,
      prompt: "Canopy soils provide vital moisture reservoirs during periods of drought.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "TRUE" },
      explanation: "Đoạn D: 'harbor specialized invertebrates and provide critical moisture reservoirs during prolonged dry spells.'",
    },
    {
      position: 24,
      type: QuestionType.TRUE_FALSE,
      prompt: "Global warming has completely halted all evapotranspiration in South American rainforests.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "NOT GIVEN" },
      explanation: "Đoạn E chỉ giải thích hiện tượng thoát hơi nước giúp làm mát khí hậu, không có thông tin nói rằng quá trình này đã bị dừng hoàn toàn.",
    },
    {
      position: 25,
      type: QuestionType.TRUE_FALSE,
      prompt: "Epiphytic orchids harm their host trees by actively feeding on internal vascular sap.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "NOT GIVEN" },
      explanation: "Bài viết chỉ nhắc đến phong lan sống trên cây giữ mùn và nước, không có thông tin phong lan hút nhựa hay làm hại cây chủ.",
    },
    {
      position: 26,
      type: QuestionType.TRUE_FALSE,
      prompt: "The destruction of the canopy framework can lead to the extinction of specialized species.",
      options: ["TRUE", "FALSE", "NOT GIVEN"],
      answerKey: { value: "TRUE" },
      explanation: "Đoạn F: 'causing cascading extinctions among specialized canopy organisms.'",
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
