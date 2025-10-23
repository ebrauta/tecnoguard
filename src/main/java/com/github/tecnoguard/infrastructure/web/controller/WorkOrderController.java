package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.domain.model.WorkOrder;
import com.github.tecnoguard.domain.service.IWorkService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workorders")
public class WorkOrderController {

    private final IWorkService service;

    public WorkOrderController(IWorkService service) {
        this.service = service;
    }

    @GetMapping
    public List<WorkOrder> list(){
        return service.list();
    }

    @GetMapping("/{id}")
    public WorkOrder get(@PathVariable Long id){
        return service.findById(id);
    }

    @PostMapping
    public WorkOrder create(@RequestBody WorkOrder order){
        return service.create(order);
    }

    @PatchMapping("/{id}/assign")
    public WorkOrder assign(@PathVariable Long id,
                            @RequestParam String tech,
                            @RequestParam String date){
        return service.assign(id, tech, LocalDate.parse(date));
    }

    @PatchMapping("/{id}/start")
    public WorkOrder start(@PathVariable Long id){
        return service.start(id);
    }

    @PatchMapping("/{id}/complete")
    public WorkOrder complete(@PathVariable Long id, @RequestParam String log){
        return service.complete(id, log);
    }

    @PatchMapping("/{id}/cancel")
    public WorkOrder cancel(@PathVariable Long id, @RequestParam String reason){
        return service.cancel(id, reason);
    }

}
