package net.devdome.bhu.model;

import java.util.ArrayList;

public class Department {


    private String code;
    private String name;

    public Department(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static ArrayList<Department> getDepartments() {
        ArrayList<Department> departments = new ArrayList<>();
        departments.add(new Department("Accounting", "ACC"));
        departments.add(new Department("Anatomy", "ANA"));
        departments.add(new Department("Biochemistry", "BCH"));
        departments.add(new Department("Business Administration", "BUS"));
        departments.add(new Department("Computer Science", "CMP"));
        departments.add(new Department("Economics", "ECO"));
        departments.add(new Department("English Language", "ENG"));
        departments.add(new Department("Industrial Chemistry", "ICH"));
        departments.add(new Department("Mass Communication", "MAC"));
        departments.add(new Department("Microbiology", "MCB"));
        departments.add(new Department("Medicine", "MCB"));
        departments.add(new Department("Physiology", "PHY"));
        departments.add(new Department("Political Science", "POL"));
        departments.add(new Department("Sociology", "SOC"));
        return departments;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
