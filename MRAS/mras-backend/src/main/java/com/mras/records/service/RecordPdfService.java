package com.mras.records.service;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import com.mras.patients.model.Patient;
import com.mras.records.model.Prescription;
import com.mras.records.model.Record;
import com.mras.records.model.TestResult;
import com.mras.records.model.Vitals;

@Service
public class RecordPdfService {

    private static final float MARGIN = 48f;
    private static final float LEADING = 14.5f;

    public byte[] buildRecordPdf(Record record, Patient patient) {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float y = PDRectangle.A4.getHeight() - MARGIN;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                cs.newLineAtOffset(MARGIN, y);
                cs.showText("Medical Record Summary");
                cs.endText();
            }

            y -= 30;

            y = writeSection(doc, page, y, "Patient", List.of(
                    "Name: " + safe(patient.getName()),
                    "MRN: " + safe(patient.getMrn()),
                    "DOB: " + (patient.getDob() != null ? patient.getDob().toString() : "-"),
                    "Gender: " + safe(patient.getGender()),
                    "Phone: " + safe(patient.getPhone())
            ));

            y = writeSection(doc, page, y, "Encounter", List.of(
                    "Record ID: " + safe(record.getId()),
                    "Encounter Date: " + formatInstant(record.getEncounterDate()),
                    "Author ID: " + safe(record.getAuthorId()),
                    "Chief Complaint: " + safe(record.getChiefComplaint())
            ));

            y = writeSection(doc, page, y, "Diagnosis", List.of(
                    record.getDiagnosis() != null && !record.getDiagnosis().isEmpty()
                            ? String.join(", ", record.getDiagnosis())
                            : "-"
            ));

            y = writeSection(doc, page, y, "Vitals", List.of(
                    formatVitals(record.getVitals())
            ));

            y = writeSection(doc, page, y, "Prescriptions", List.of(
                    formatPrescriptions(record.getPrescriptions())
            ));

            y = writeSection(doc, page, y, "Tests", List.of(
                    formatTests(record.getTests())
            ));

            y = writeSection(doc, page, y, "Notes", List.of(
                    record.getNotes() != null && !record.getNotes().isBlank()
                            ? record.getNotes()
                            : "-"
            ));

            doc.save(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private float writeSection(PDDocument doc, PDPage page, float y, String title, List<String> lines)
            throws Exception {

        if (y < 140) {
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            y = PDRectangle.A4.getHeight() - MARGIN;
        }

        try (PDPageContentStream cs = new PDPageContentStream(
                doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 13);
            cs.newLineAtOffset(MARGIN, y);
            cs.showText(title);
            cs.endText();

            y -= 18;

            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            cs.newLineAtOffset(MARGIN, y);

            for (String raw : lines) {
                for (String wrapped : wrap(raw, 95)) {
                    cs.showText(wrapped);
                    cs.newLineAtOffset(0, -LEADING);
                    y -= LEADING;

                    if (y < 80) break;
                }
                if (y < 80) break;
            }
            cs.endText();
        }

        return y - 10;
    }

    private static String formatInstant(java.time.Instant instant) {
        if (instant == null) return "-";
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .format(instant.atZone(ZoneId.systemDefault()));
    }

    private static String formatVitals(Vitals v) {
        if (v == null) return "-";
        StringBuilder sb = new StringBuilder();
        if (v.getBp() != null) sb.append("BP: ").append(v.getBp()).append("  ");
        if (v.getPulse() != null) sb.append("Pulse: ").append(v.getPulse()).append("  ");
        if (v.getTempC() != null) sb.append("Temp: ").append(v.getTempC()).append("  ");
        if (v.getSpo2() != null) sb.append("SpO2: ").append(v.getSpo2()).append("  ");
        return sb.toString().trim().isEmpty() ? "-" : sb.toString().trim();
    }

    private static String formatPrescriptions(List<Prescription> ps) {
        if (ps == null || ps.isEmpty()) return "-";
        return ps.stream()
                .map(p -> String.format(
                        "%s (%s) %s for %s days",
                        safe(p.getDrug()),
                        safe(p.getDose()),
                        safe(p.getFrequency()),
                        safe(p.getDays())
                ))
                .reduce((a, b) -> a + " | " + b)
                .orElse("-");
    }

    private static String formatTests(List<TestResult> ts) {
        if (ts == null || ts.isEmpty()) return "-";
        return ts.stream()
                .map(t -> safe(t.getName()) + ": " + safe(t.getResult()))
                .reduce((a, b) -> a + " | " + b)
                .orElse("-");
    }

    private static String safe(Object v) {
        if (v == null) return "-";
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? "-" : s;
    }

    private static List<String> wrap(String text, int maxLen) {
        if (text == null) return List.of("-");
        if (text.length() <= maxLen) return List.of(text);

        java.util.ArrayList<String> out = new java.util.ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();

        for (String w : words) {
            if (line.length() == 0) {
                line.append(w);
            } else if (line.length() + 1 + w.length() <= maxLen) {
                line.append(" ").append(w);
            } else {
                out.add(line.toString());
                line = new StringBuilder(w);
            }
        }
        if (line.length() > 0) out.add(line.toString());

        return out;
    }
}
