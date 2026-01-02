package org.lassevonpfeil.hdgroup.command;

import org.lassevonpfeil.hdgroup.service.MarkerService;

public class SetMarkerCommand implements Command{
    private final MarkerService markerService;
    private final int pageIndex;
    private boolean existedBefore;

    public SetMarkerCommand(MarkerService markerService, int pageIndex) {
        this.markerService = markerService;
        this.pageIndex = pageIndex;
    }

    @Override
    public void execute() {
        existedBefore = markerService.hasMarker(pageIndex);
        markerService.setMarker(pageIndex);
    }

    @Override
    public void undo() {
        if (!existedBefore) {
            markerService.removeMarker(pageIndex);
        }
    }
}
