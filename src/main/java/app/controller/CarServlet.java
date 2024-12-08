package app.controller;

import app.model.Car;
import app.repository.CarRepository;
import app.repository.CarRepositoryDB;
import app.repository.CarRepositoryHibernate;
import app.repository.CarRepositoryMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CarServlet extends HttpServlet {

    private CarRepository repository = new CarRepositoryHibernate();
    ObjectMapper mapper = new ObjectMapper();


    // GET http://10.2.3.4:8080/cars

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String[]> params = req.getParameterMap();

        if (params.isEmpty()) {
            List<Car> cars = repository.getAll();

            resp.setContentType("application/json");

            String json = mapper.writeValueAsString(cars);
            resp.getWriter().write(json);
        } else {
            String idStr = params.get("id")[0];
            Long id = Long.parseLong(idStr);
            Car car = repository.findById(id);
            if (car == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Car not found");
            } else {
                String jsonResponse = mapper.writeValueAsString(car);
                resp.getWriter().write(jsonResponse);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            Car car = mapper.readValue(req.getReader(), Car.class);

            if (car.getId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("ID must be specified for update");
                return;
            }

            Car existingCar = repository.findById(car.getId());
            if (existingCar == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Car with id " + car.getId() + " not found");
                return;
            }

            Car updatedCar = repository.update(car);

            String jsonResponse = mapper.writeValueAsString(updatedCar);
            resp.getWriter().write(jsonResponse);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An error occurred while updating the car: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            Car car = mapper.readValue(req.getReader(), Car.class);
            car = repository.save(car);
            String json = mapper.writeValueAsString(car);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(json);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("ID must be specified for delete");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            if (id <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("ID must be positive");
            } else {
                repository.delete(id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid ID format. ID must be a number");
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

}
