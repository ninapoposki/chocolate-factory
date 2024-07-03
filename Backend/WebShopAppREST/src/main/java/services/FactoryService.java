package services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Comment;
import beans.Factory;
import beans.Location;
import beans.User;
import dao.ChocolateDAO;
import dao.CommentDAO;
import dao.FactoryDAO;
import dao.LocationDAO;
import dao.UserDAO;
import enumerations.Role;

@Path("/factories")
public class FactoryService {
    @Context
    ServletContext ctx;

    public FactoryService() {
    }

    @PostConstruct
    public void init() {
        String contextPath = ctx.getRealPath("");
        if (ctx.getAttribute("factoryDAO") == null) {
            LocationDAO locationDAO = new LocationDAO();
            ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath);
            UserDAO userDAO = new UserDAO(contextPath);
            FactoryDAO factoryDAO = new FactoryDAO(contextPath, locationDAO, chocolateDAO, userDAO);
            
            ctx.setAttribute("factoryDAO", factoryDAO);
            ctx.setAttribute("userDAO", userDAO);

            chocolateDAO.setFactoryDAO(factoryDAO);

            chocolateDAO.loadChocolates(contextPath);

            factoryDAO.loadFactories(contextPath);
        } else {
            System.out.println("FactoryDAO already initialized.");
        }

        if (ctx.getAttribute("userDAO") == null) {
            UserDAO userDAO = new UserDAO(contextPath);
            ctx.setAttribute("userDAO", userDAO);
            System.out.println("UserDAO initialized.");
        } else {
            System.out.println("UserDAO already initialized.");
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Factory> getFactories() {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.findAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Factory findFactory(@PathParam("id") String id) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.findFactory(id);
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchFactories(@QueryParam("search") String search) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        Collection<Factory> factories = dao.searchFactories(search);
        if (factories.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.ok(factories).build();
    }


    @GET
    @Path("/sort")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sortFactories(@QueryParam("sortBy") String sortBy, @QueryParam("ascending") boolean ascending) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        Collection<Factory> factories = dao.sortFactories(sortBy, ascending);
        return Response.ok(factories).build();
    }

    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterFactories(@QueryParam("chocolateType") String chocolateType, 
                                    @QueryParam("chocolateVariety") String chocolateVariety, 
                                    @QueryParam("openOnly") Boolean openOnly) {
        System.out.println("Filtering with params: chocolateType=" + chocolateType 
                           + ", chocolateKind=" + chocolateVariety
                           + ", openOnly=" + openOnly);
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        Collection<Factory> factories = dao.filterFactories(chocolateType, chocolateVariety, openOnly);
        return Response.ok(factories).build();
    }

    @GET
    @Path("/unassignedManagers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnassignedManagers() {
        FactoryDAO factoryDao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        List<User> unassignedManagers = factoryDao.findUnassignedManagers();
        return Response.ok(unassignedManagers).build();
    }

    @POST
    @Path("/createWithManager")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createFactoryWithManager(Factory factory) {
        System.out.println("Creating factory with manager...");

        FactoryDAO factoryDao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");

        if (factoryDao == null) {
            System.out.println("FactoryDAO is null.");
        }
        if (userDao == null) {
            System.out.println("UserDAO is null.");
        }

        User manager = factory.getUser();
        if (manager != null && (manager.getId() == null || manager.getId().isEmpty())) {
            System.out.println("Creating new manager: " + manager.getUsername());
            manager.setRole(Role.MANAGER);
            User savedManager = userDao.save(manager);
            if (savedManager == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add manager").build();
            }
            factory.setUser(savedManager);
        } else if (manager != null) {
            System.out.println("Assigning existing manager: " + manager.getId());
            User existingManager = userDao.findUser(manager.getId());
            if (existingManager == null || existingManager.getRole() != Role.MANAGER) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid manager ID").build();
            }
            factory.setUser(existingManager);
        }

        if (factory.getLocation() != null && factory.getLocation().getId() == null) {
            System.out.println("Saving new location.");
            Location newLocation = factoryDao.getLocationDAO().save(factory.getLocation());
            factory.setLocation(newLocation);
        }

        factory.setIsStatus(true);
        factory.setGrade(0);
        Factory savedFactory = factoryDao.save(factory);
        System.out.println("Factory created: " + savedFactory.getFactoryName());
        return Response.status(Response.Status.CREATED).entity(savedFactory).build();
    }
    
    @GET
    @Path("/factoriesForManager/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFactoriesForManager(@PathParam("userId") String userId) {
        FactoryDAO factoryDao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        Collection<Factory> factories = factoryDao.findFactoriesByUserId(userId);
        return Response.ok(factories).build();
    }
    
    @POST
    @Path("/addEmployee/{factoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addEmployeeToFactory(@PathParam("factoryId") String factoryId, User employee) {
        FactoryDAO factoryDao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");

        if (employee.getId() == null || employee.getId().isEmpty()) {
            User savedEmployee = userDao.save(employee);
            if (savedEmployee == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add employee").build();
            }
            employee = savedEmployee;
        } else {
            User existingEmployee = userDao.findUser(employee.getId());
            if (existingEmployee == null || existingEmployee.getRole() != Role.EMPLOYEE) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid employee ID").build();
            }
            employee = existingEmployee;
        }

        Factory updatedFactory = factoryDao.addEmployeeToFactory(factoryId, employee);
        if (updatedFactory == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Factory not found").build();
        }
        return Response.ok(updatedFactory).build();
    }

    @GET
    @Path("/employeeFactory/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFactoryByEmployee(@PathParam("userId") String userId) {
        FactoryDAO factoryDao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        Factory factory = factoryDao.findFactoryByUserId(userId);
        if (factory == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Factory not found for the given employee").build();
        }
        return Response.ok(factory).build();
    }
    
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFactory(@PathParam("id") String id, Factory factory) {
    	 FactoryDAO factoryDao = (FactoryDAO) ctx.getAttribute("factoryDAO");;
        Factory updatedFactory = factoryDao.updateFactory(id, factory);

        if (updatedFactory != null) {
            return Response.ok(updatedFactory).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Factory not found").build();
        }
    }
    
    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchFactory(@PathParam("id") String id, Map<String, Double> patchData) {
        FactoryDAO factoryDao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        Factory factory = factoryDao.findFactory(id);

        if (factory == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Factory not found").build();
        }

        if (patchData.containsKey("grade")) {
            double newGrade = patchData.get("grade");
            factory.setGrade(newGrade);
            Factory updatedFactory = factoryDao.updateFactory(id, factory);
            return Response.ok(updatedFactory).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid patch data").build();
        }
    }
}
