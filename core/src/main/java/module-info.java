open module core {
    requires com.fasterxml.jackson.databind;
    requires jakarta.persistence;
    requires jakarta.validation;
    requires static lombok;
    requires spring.data.jpa;
    requires spring.context;
    requires org.hibernate.orm.core;

    exports net.azurewebsites.planner.core.Models;
    exports net.azurewebsites.planner.core.Repositories;
}