package app.repository;


import app.model.Car;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static app.constants.Constants.*;

public class CarRepositoryDB implements CarRepository {

    private Connection getConnection() {
        try {
            Class.forName(DB_DRIVER_PATH);

            // jdbc:postgres://10.2.3.4:5432/g_49_cars?user=my_user&password=pos1234
            String dbUrl = String.format("%s%s?user=%s&password=%s",
                    DB_ADDRESS, DB_NAME, DB_USER, DB_PASSWORD);

            return DriverManager.getConnection(dbUrl);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Car> getAll() {
            return null;
    }

    @Override
        public Car findById ( long id){
            return null;
        }

        @Override
        public Car save (Car car){

            try (Connection connection = getConnection();
            ) {

                // INSERT INTO cars (brand, price, year) VALUES ('Toyota', 35000, 2022);
                String query = String.format("INSERT INTO cars_table (brand, price, year) VALUES ('%s', %s, %d);", car.getBrand(), car.getPrice(), car.getYear());

                Statement statement = connection.createStatement();

                statement.execute(query, Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSet = statement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong("id");
                car.setId(id);


                return car;


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Car update (Car car){
            try (Connection connection = getConnection();
            ) {

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return null;
        }

        @Override
        public void delete ( long id){
            try (Connection connection = getConnection();
            ) {

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }
    }
