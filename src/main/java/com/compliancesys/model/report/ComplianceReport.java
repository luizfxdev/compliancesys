package com.compliancesys.model.report;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.compliancesys.model.ComplianceAudit;

/**
 * Representa um relatório de conformidade detalhado para um motorista ou geral.
 * Contém informações sumarizadas e uma lista de auditorias.
 */
public class ComplianceReport {
    private String reportName;
    private LocalDate generatedDate;
    private int driverId; // Pode ser 0 ou -1 para relatórios gerais
    private String driverName; // Pode ser null para relatórios gerais
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalAudits;
    private int compliantAudits;
    private int nonCompliantAudits;
    private double complianceRate;
    private List<ComplianceAudit> audits; // Auditorias detalhadas incluídas no relatório

    public ComplianceReport() {
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
