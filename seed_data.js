/**
 * VidyaSetu Seed Script
 * Populates realistic courses, subjects, chapters, lessons, and sample questions for:
 * - Class 10 (CBSE Mathematics & Science)
 * - Class 11 (Physics & Chemistry)
 * - Class 12 (Board & JEE Electrostatics)
 * - Competitive Exam (JEE Advanced Rank Booster)
 * - Bachelor's Track (Data Structures & Algorithms in Java/C++)
 */

const admin = require('firebase-admin');

if (!admin.apps.length) {
  admin.initializeApp();
}

const db = admin.firestore();

const SAMPLE_COURSES = [
  {
    id: "c10_maths_boards",
    title: "Class 10 CBSE Maths - 100/100 Masterclass",
    instructorName: "Prof. R. K. Sharma",
    instructorBio: "22+ years teaching CBSE Board toppers",
    targetLevel: "CLASS_10",
    stream: "GENERAL",
    rating: 4.9,
    reviewCount: 1840,
    priceInr: 0,
    originalPriceInr: 1999,
    badgeLabel: "Free Access",
    description: "Complete coverage of Real Numbers, Polynomials, Quadratic Equations, Triangles, Trigonometry, and NCERT Exemplar problems.",
    validityDays: 365,
    certificateIncluded: true,
    published: true,
    schemaVersion: 1
  },
  {
    id: "c12_physics_boards_jee",
    title: "Class 12 Physics - Electrostatics & Optics",
    instructorName: "Er. Saurabh Verma (IIT Roorkee)",
    instructorBio: "Top physics mentor with 100+ selections in IITs",
    targetLevel: "CLASS_12",
    stream: "SCIENCE_PCM",
    rating: 4.95,
    reviewCount: 3450,
    priceInr: 999,
    originalPriceInr: 4999,
    badgeLabel: "Bestseller",
    description: "Master Electric Charges, Gauss Law, Potential, Capacitance, Magnetism, and Wave Optics with step-by-step numerical derivations.",
    validityDays: 365,
    certificateIncluded: true,
    published: true,
    schemaVersion: 1
  },
  {
    id: "deg_dsa_interview_prep",
    title: "Data Structures & Algorithms in Java & C++",
    instructorName: "Rohan Deshmukh",
    instructorBio: "Ex-Google Software Engineer & LeetCode Guardian",
    targetLevel: "BACHELORS",
    stream: "ENGINEERING_CS",
    rating: 4.96,
    reviewCount: 5200,
    priceInr: 0,
    originalPriceInr: 4999,
    badgeLabel: "Flagship",
    description: "Arrays, Strings, LinkedList, Trees, Graphs, Dynamic Programming, System Design basics, and Top 200 FAANG interview questions.",
    validityDays: 365,
    certificateIncluded: true,
    published: true,
    schemaVersion: 1
  }
];

async function seedData() {
  console.log("Seeding VidyaSetu course curriculum into Firestore...");
  const batch = db.batch();

  for (const course of SAMPLE_COURSES) {
    const courseRef = db.collection('courses').doc(course.id);
    batch.set(courseRef, course, { merge: true });

    // Seed Chapter 1
    const chapterRef = courseRef.collection('subjects').doc('main_subject')
      .collection('chapters').doc('ch_1');

    batch.set(chapterRef, {
      id: 'ch_1',
      title: 'Foundation & Core Principles',
      orderIndex: 1
    }, { merge: true });

    // Seed Lessons
    const lesson1Ref = chapterRef.collection('lessons').doc('l_1');
    batch.set(lesson1Ref, {
      id: 'l_1',
      title: 'Concept Introduction & Theory Breakdown',
      type: 'VIDEO',
      orderIndex: 1,
      durationSec: 1320,
      isFreePreview: true,
      storagePath: `courses/${course.id}/videos/l_1.mp4`,
      pdfPageCount: 0
    }, { merge: true });

    const lesson2Ref = chapterRef.collection('lessons').doc('l_2');
    batch.set(lesson2Ref, {
      id: 'l_2',
      title: 'Revision Formula Sheet & Mind Map',
      type: 'PDF_NOTES',
      orderIndex: 2,
      durationSec: 600,
      isFreePreview: true,
      storagePath: `courses/${course.id}/notes/l_2_notes.pdf`,
      pdfPageCount: 12
    }, { merge: true });
  }

  await batch.commit();
  console.log("✅ Seed completed successfully.");
}

if (require.main === module) {
  seedData().catch(console.error);
}

module.exports = { seedData };
