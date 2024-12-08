package app.repository;

import app.model.Car;

import java.util.List;

public interface CarRepository {

    List<Car> getAll();

    Car findById(long id);

    Car save(Car car);

    Car update(Car car);

    void delete(long id);
}
