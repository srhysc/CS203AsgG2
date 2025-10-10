package com.cs203.grp2.Asg2.models;

public class TariffLatest {
  private Integer year;
  private Double simpleAverage;
  private String reporter;
  private String partner;
  private String product;
  private String datatype;

  public TariffLatest() {}
  public TariffLatest(Integer year, Double simpleAverage, String reporter,
                      String partner, String product, String datatype) {
    this.year = year; this.simpleAverage = simpleAverage;
    this.reporter = reporter; this.partner = partner;
    this.product = product; this.datatype = datatype;
  }
  public Integer getYear() { return year; }
  public Double getSimpleAverage() { return simpleAverage; }
  public String getReporter() { return reporter; }
  public String getPartner() { return partner; }
  public String getProduct() { return product; }
  public String getDatatype() { return datatype; }
}
