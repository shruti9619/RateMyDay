package com.learn.shruti.ratemyday.Model;

/**
 * Created by Shruti on 18/07/2017.
 */
public class Employee {

    public String employeeID;
    public String empName;
    public String designation;
    public String empEmail;
    public Long Phone;
    public String Password;


    public Employee(String employeeID, String empName, String designation, String empEmail, Long Phone, String pwd) {
        this.employeeID = employeeID;
        this.empName = empName;
        this.designation = designation;
        this.empEmail = empEmail;
        this.Phone = Phone;
        this.Password = pwd;
    }


    public Employee() {
    }
}
