public abstract class Employee
{
    private String name;
    private int salary;
    public Employee(String name, int salary)
    {
        this.name = name; this.salary = salary;
    }
    public String getName() { return name; }
    public abstract int getSalary();
    public void raiseSalary(double pct)
    {
        //salary *= (1 + pct/100);
        salary = salary + (int)(pct/100 * salary);
    }
    public static void main(String[] args)
    {
        Employee emp = new Employee("Test", 4000);
        System.out.println("Name: " + emp.getName() + " Salary: " + emp.getSalary());
        emp.raiseSalary(15);
        System.out.println("Name: " + emp.getName() + " Salary: " + emp.getSalary());
    }
}