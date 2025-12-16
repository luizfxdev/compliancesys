package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Vehicle {
    private int id;
    private String plate;
    private String manufacturer;
    private String model;
    private int year;
    private int companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Vehicle() {
    }

    public Vehicle(int id, String plate, String manufacturer, String model, int year, int companyId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.plate = plate;
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.companyId = companyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Vehicle(String plate, String manufacturer, String model, int year, int companyId) {
        this(0, plate, manufacturer, model, year, companyId, null, null);
    }

    public Vehicle(int id, String plate, String manufacturer, String model, int year, int companyId) {
        this(id, plate, manufacturer, model, year, companyId, null, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", plate='" + plate + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", companyId=" + companyId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id &&
                year == vehicle.year &&
                companyId == vehicle.companyId &&
                Objects.equals(plate, vehicle.plate) &&
                Objects.equals(manufacturer, vehicle.manufacturer) &&
                Objects.equals(model, vehicle.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, plate, manufacturer, model, year, companyId);
    }
}
