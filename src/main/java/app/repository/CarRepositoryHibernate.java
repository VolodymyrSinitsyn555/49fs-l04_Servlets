package app.repository;

import app.model.Car;
import jakarta.persistence.EntityManager;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.util.List;


public class CarRepositoryHibernate implements CarRepository {

    EntityManager entityManager;

    public CarRepositoryHibernate() {
        entityManager = new Configuration()
                .configure("hibernate/postgres.cfg.xml")
                .buildSessionFactory().createEntityManager();
    }

    @Override
    public List<Car> getAll() {
        return entityManager.createQuery("from Car", Car.class).getResultList();
    }

    @Override
    public Car findById(long id) {
        return entityManager.find(Car.class, id);
    }

    @Override
    public Car save(Car car) {

        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }

        if (car.getBrand() == null || car.getBrand().isEmpty()) {
            throw new IllegalArgumentException("Brand cannot be empty or null");
        }
        if (car.getPrice() == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }

        if (car.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        entityManager.getTransaction().begin();
        entityManager.persist(car);
        entityManager.getTransaction().commit();
        return car;

    }

    private boolean isFieldsEmptyForUpdate(Car car) {
        return car.getPrice() == null && car.getYear() == 0;
    }

    private void updateFields(Car existingCar, Car car) {
        if (car.getPrice() != null) {
            existingCar.setPrice(car.getPrice());
        }
        if (car.getYear() != 0) {
            existingCar.setYear(car.getYear());
        }
    }

    @Override
    public Car update(Car car) {

        if (car.getId() == null) {
            throw new IllegalArgumentException("ID must be specified for update");
        }

        Car existingCar = entityManager.find(Car.class, car.getId());
        if (existingCar == null) {
            throw new IllegalArgumentException("Car with id " + car.getId() + " not found");
        }

        if (isFieldsEmptyForUpdate(car)) {
            throw new IllegalArgumentException("No fields with new values were provided for update. Please specify at least one field to update.");
        }

        entityManager.getTransaction().begin();
        updateFields(existingCar, car);
        entityManager.getTransaction().commit();
        return existingCar;
    }

    @Override
    public void delete(long id) {

        entityManager.getTransaction().begin();
        Car car = entityManager.find(Car.class, id);
        if (car != null) {
            entityManager.remove(car);
        } else {
            throw new IllegalArgumentException("Car with id " + id + " not found");
        }
        entityManager.getTransaction().commit();
    }
}
