// src/main/java/com/compliancesys/model/report/ComplianceReport.java
package com.compliancesys.model.report;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.compliancesys.model.ComplianceAudit;

/**
 * Representa um relatório de conformidade.
 * Contém informações agregadas sobre auditorias de conformidade.
 */
public class ComplianceReport {
    private String reportName;
    private LocalDate generatedDate;
    private LocalDateTime generatedAt;
    private int driverId;
    private String driverName;
    private LocalDate startDate;
    private LocalDate endDate;
    private long totalJourneys;
    private long compliantJourneys;
    private long nonCompliantJourneys;
    private int totalAudits;
    private int compliantAudits;
    private int nonCompliantAudits;
    private double complianceRate;
    private List<String> violations; // Lista de violações agregadas
    private Duration totalWorkDuration; // Duração total de trabalho agregada
    private Duration totalDrivingDuration; // Duração total de direção agregada
    private List<ComplianceAudit> audits; // Lista de auditorias individuais

    public ComplianceReport() {
    }

    // Construtor completo
    public ComplianceReport(String reportName, LocalDate generatedDate,
                            LocalDateTime generatedAt, int driverId, String driverName,
                            LocalDate startDate, LocalDate endDate,
                            long totalJourneys, long compliantJourneys, long nonCompliantJourneys,
                            int totalAudits, int compliantAudits, int nonCompliantAudits,
                            double complianceRate, List<String> violations,
                            Duration totalWorkDuration, Duration totalDrivingDuration,
                            List<ComplianceAudit> audits) {
        this.reportName = reportName;
        this.generatedDate = generatedDate;
        this.generatedAt = generatedAt;
        this.driverId = driverId;
        this.driverName = driverName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalJourneys = totalJourneys;
        this.compliantJourneys = compliantJourneys;
        this.nonCompliantJourneys = nonCompliantJourneys;
        this.totalAudits = totalAudits;
        this.compliantAudits = compliantAudits;
        this.nonCompliantAudits = nonCompliantAudits;
        this.complianceRate = complianceRate;
        this.violations = violations;
        this.totalWorkDuration = totalWorkDuration;
        this.totalDrivingDuration = totalDrivingDuration;
        this.audits = audits;
    }

    // Getters e Setters
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
        if (generatedAt != null) {
            this.generatedDate = generatedAt.toLocalDate();
        }
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public long getTotalJourneys() {
        return totalJourneys;
    }

    public void setTotalJourneys(long totalJourneys) {
        this.totalJourneys = totalJourneys;
    }

    public long getCompliantJourneys() {
        return compliantJourneys;
    }

    public void setCompliantJourneys(long compliantJourneys) {
        this.compliantJourneys = compliantJourneys;
    }

    public long getNonCompliantJourneys() {
        return nonCompliantJourneys;
    }

    public void setNonCompliantJourneys(long nonCompliantJourneys) {
        this.nonCompliantJourneys = nonCompliantJourneys;
    }

    public int getTotalAudits() {
        return totalAudits;
    }

    public void setTotalAudits(int totalAudits) {
        this.totalAudits = totalAudits;
    }

    public int getCompliantAudits() {
        return compliantAudits;
    }

    public void setCompliantAudits(int compliantAudits) {
        this.compliantAudits = compliantAudits;
    }

    public int getNonCompliantAudits() {
        return nonCompliantAudits;
    }

    public void setNonCompliantAudits(int nonCompliantAudits) {
        this.nonCompliantAudits = nonCompliantAudits;
    }

    public double getComplianceRate() {
        return complianceRate;
    }

    public void setComplianceRate(double complianceRate) {
        this.complianceRate = complianceRate;
    }

    public List<String> getViolations() {
        return violations;
    }

    public void setViolations(List<String> violations) {
        this.violations = violations;
    }

    public Duration getTotalWorkDuration() {
        return totalWorkDuration;
    }

    public void setTotalWorkDuration(Duration totalWorkDuration) {
        this.totalWorkDuration = totalWorkDuration;
    }

    public Duration getTotalDrivingDuration() {
        return totalDrivingDuration;
    }

    public void setTotalDrivingDuration(Duration totalDrivingDuration) {
        this.totalDrivingDuration = totalDrivingDuration;
    }

    public List<ComplianceAudit> getAudits() {
        return audits;
    }

    public void setAudits(List<ComplianceAudit> audits) {
        this.audits = audits;
    }

    @Override
    public String toString() {
        return "ComplianceReport{" +
                "reportName='" + reportName + '\'' +
                ", generatedDate=" + generatedDate +
                ", generatedAt=" + generatedAt +
                ", driverId=" + driverId +
                ", driverName='" + driverName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalJourneys=" + totalJourneys +
                ", compliantJourneys=" + compliantJourneys +
                ", nonCompliantJourneys=" + nonCompliantJourneys +
                ", totalAudits=" + totalAudits +
                ", compliantAudits=" + compliantAudits +
                ", nonCompliantAudits=" + nonCompliantAudits +
                ", complianceRate=" + complianceRate +
                ", violations=" + violations +
                ", totalWorkDuration=" + totalWorkDuration +
                ", totalDrivingDuration=" + totalDrivingDuration +
                ", audits=" + audits +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceReport that = (ComplianceReport) o;
        return driverId == that.driverId &&
                totalJourneys == that.totalJourneys &&
                compliantJourneys == that.compliantJourneys &&
                nonCompliantJourneys == that.nonCompliantJourneys &&
                totalAudits == that.totalAudits &&
                compliantAudits == that.compliantAudits &&
                nonCompliantAudits == that.nonCompliantAudits &&
                Double.compare(that.complianceRate, complianceRate) == 0 &&
                Objects.equals(reportName, that.reportName) &&
                Objects.equals(generatedDate, that.generatedDate) &&
                Objects.equals(generatedAt, that.generatedAt) &&
                Objects.equals(driverName, that.driverName) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(violations, that.violations) &&
                Objects.equals(totalWorkDuration, that.totalWorkDuration) &&
                Objects.equals(totalDrivingDuration, that.totalDrivingDuration) &&
                Objects.equals(audits, that.audits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportName, generatedDate, generatedAt, driverId, driverName,
                startDate, endDate, totalJourneys, compliantJourneys,
                nonCompliantJourneys, totalAudits, compliantAudits,
                nonCompliantAudits, complianceRate, violations,
                totalWorkDuration, totalDrivingDuration, audits);
    }
}
