package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.core.utils.NoteFormatter;
import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.models.WorkOrderNote;
import com.github.tecnoguard.domain.service.IWorkOrderNoteService;
import com.github.tecnoguard.infrastructure.persistence.WorkOrderNoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderNoteServiceImpl implements IWorkOrderNoteService {

    private final WorkOrderNoteRepository repo;
    private final NoteFormatter formatter;

    public WorkOrderNoteServiceImpl(WorkOrderNoteRepository repo, NoteFormatter formatter) {
        this.repo = repo;
        this.formatter = formatter;
    }

    @Override
    public Page<WorkOrderNote> listNotes(Long workorderId, Pageable pageable) {
        return repo.findByWorkOrderIdOrderByCreatedAtDesc(workorderId, pageable);
    }

    @Override
    public WorkOrderNote addNote(WorkOrder wo, String message, String author) {
        WorkOrderNote note = new WorkOrderNote();
        note.setWorkOrder(wo);
        note.setAuthor(author);
        note.setMessage(formatter.format(message, author));
        return repo.save(note);
    }

    @Override
    public WorkOrderNote addSystemNote(WorkOrder wo, String message, String author) {
        return addNote(wo, message, author);
    }
}
