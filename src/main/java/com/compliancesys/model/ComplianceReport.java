package com.compliancesys.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Representa um relatório de conformidade para um motorista em um período específico.
 */
public class ComplianceReport {
    private int driverId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalAudits;
    private int compliantCount;
    private int nonCompliantCount;
    private List<ComplianceAudit> audits;

    public ComplianceReport() {
    }

    public ComplianceReport(int driverId, LocalDate startDate, LocalDate endDate, int totalAudits, int compliantCount, int nonCompliantCount, List<ComplianceAudit> audits) {
        this.driverId = driverId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAudits = totalAudits;
        this.compliantCount = compliantCount;
        this.nonCompliantCount = nonCompliantCount;
        this.audits = audits;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
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

    public int getCompliantCount() {
        return compliantCount;
    }

    public void setCompliantCount(int compliantCount) {
        this.compliantCount = compliantCount;
    }

    public int getNonCompliantCount() {
        return nonCompliantCount;
    }

    public void setNonCompliantCount(int nonCompliantCount) {
        this.nonCompliantCount = nonCompliantCount;
    }

    public List<ComplianceAudit> getAudits() {
        return audits;
    }

    public void setAudits(List<ComplianceAudit> audits) {
        this.audits = audits;
    }

    public double getComplianceRate() {
        if (totalAudits == 0) {
            return 0.0;
        }
        return (double) compliantCount / totalAudits * 100.0;
    }

    @Override
    public String toString() {
        return "ComplianceReport{" +
                "driverId=" + driverId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalAudits=" + totalAudits +
                ", compliantCount=" + compliantCount +
                ", nonCompliantCount=" + nonCompliantCount +
                ", complianceRate=" + String.format("%.2f%%", getComplianceRate()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceReport that = (ComplianceReport) o;
        return driverId == that.driverId &&
                totalAudits == that.totalAudits &&
                compliantCount == that.compliantCount &&
                nonCompliantCount == that.nonCompliantCount &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, startDate, endDate, totalAudits, compliantCount, nonCompliantCount);
    }
}