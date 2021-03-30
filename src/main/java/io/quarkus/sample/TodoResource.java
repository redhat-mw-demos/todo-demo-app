package io.quarkus.sample;

import io.quarkus.panache.common.Sort;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Todo Resource", description = "All Todo Operations")
public class TodoResource {

    @OPTIONS
    @Operation(hidden = true)
    public Response opt() {
        return Response.ok().build();
    }

    @GET
    @Operation(description = "Get all the todos")
    @Counted(name = "getAll", description = "List all To-DO items")
    @Timed(name = "checksTimerAll", description = "How long it takes to perform getAll()", unit = MetricUnits.MILLISECONDS)
    public List<Todo> getAll() {
        return Todo.listAll(Sort.by("order"));
    }

    @GET
    @Path("/{id}")
    @Operation(description = "Get a specific todo by id")
    public Todo getOne(@PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
        return entity;
    }

    @POST
    @Transactional
    @Operation(description = "Create a new todo")
    @Counted(name = "create", description = "Create a new To-DO item")
    @Timed(name = "checksTimerCreate", description = "How long it takes to perform create()", unit = MetricUnits.MILLISECONDS)
    public Response create(@Valid Todo item) {
        item.persist();
        return Response.status(Status.CREATED).entity(item).build();
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    @Operation(description = "Update an exiting todo")
    public Response update(@Valid Todo todo, @PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        entity.id = id;
        entity.completed = todo.completed;
        entity.order = todo.order;
        entity.title = todo.title;
        entity.url = todo.url;
        return Response.ok(entity).build();
    }

    @DELETE
    @Transactional
    @Operation(description = "Remove all completed todos")
    public Response deleteCompleted() {
        Todo.deleteCompleted();
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(description = "Delete a specific todo")
    public Response deleteOne(@PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
        entity.delete();
        return Response.noContent().build();
    }

}