package com.compliancesys.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Representa um relatório de conformidade.
 * Contém informações agregadas sobre auditorias de conformidade.
 */
public class ComplianceReport {
    private String reportName;
    private LocalDate generatedDate;
    private int driverId;
    private String driverName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalAudits;
    private int compliantAudits;
    private int nonCompliantAudits;
    private double complianceRate;
    private List<ComplianceAudit> audits; // Detalhes das auditorias incluídas no relatório

    public ComplianceReport() {
    }

    // Construtor completo (opcional, mas útil)
    public ComplianceReport(String reportName, LocalDate generatedDate, int driverId, String driverName,
                            LocalDate startDate, LocalDate endDate, int totalAudits, int compliantAudits,
                            int nonCompliantAudits, double complianceRate, List<ComplianceAudit> audits) {
        this.reportName = reportName;
        this.generatedDate = generatedDate;
        this.driverId = driverId;
        this.driverName = driverName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAudits = totalAudits;
        this.compliantAudits = compliantAudits;
        this.nonCompliantAudits = nonCompliantAudits;
        this.complianceRate = complianceRate;
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
                ", driverId=" + driverId +
                ", driverName='" + driverName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalAudits=" + totalAudits +
                ", compliantAudits=" + compliantAudits +
                ", nonCompliantAudits=" + nonCompliantAudits +
                ", complianceRate=" + complianceRate +
                ", audits=" + audits +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceReport that = (ComplianceReport) o;
        return driverId == that.driverId &&
                totalAudits == that.totalAudits &&
                compliantAudits == that.compliantAudits &&
                nonCompliantAudits == that.nonCompliantAudits &&
                Double.compare(that.complianceRate, complianceRate) == 0 &&
                Objects.equals(reportName, that.reportName) &&
                Objects.equals(generatedDate, that.generatedDate) &&
                Objects.equals(driverName, that.driverName) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(audits, that.audits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportName, generatedDate, driverId, driverName, startDate, endDate, totalAudits, compliantAudits, nonCompliantAudits, complianceRate, audits);
    }
}
