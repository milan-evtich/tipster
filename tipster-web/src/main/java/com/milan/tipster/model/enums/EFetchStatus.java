package com.milan.tipster.model.enums;

import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Tip;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

public enum EFetchStatus {

    NOT_FETCHED,
    PARTLY_FETCHED,
    FULLY_FETCHED;

    public static boolean needsFetching(Tip tip) {
        Objects.requireNonNull(tip, "Tip!");

        if (tip.getPick() == null || tip.getPick().equals(EPick.UNKNOWN)) {
            return true;
        }
        return needsFetching(tip.getPick())
                && needsFetching(tip.getStatus())
                && needsFetching(tip.getFetchStatus());
    }

    public static boolean needsFetching(Competition competition) {
        Objects.requireNonNull(competition, "Competition!");
        return needsFetching(competition.getFetchStatus());
    }

    public static boolean needsFetching(EPick pick) {
        Objects.requireNonNull(pick, "Pick!");
        switch (pick) {
            case SPOT_1:
            case SPOT_2:
            case SPOT_X:
            case SPOT_DNB_1:
            case SPOT_DNB_2:
            case NOBET:
                return false;

            case UNKNOWN:
            case TODO:
            default:
                return true;
        }
    }

    public static boolean needsFetching(EFetchStatus status) {
        if (status == null) {
            return true;
        } else if (status.equals(EFetchStatus.FULLY_FETCHED)) {
            return false;
        } else {
            return true;
        }
    }

    public static EFetchStatus makeFetchStatus(Object... fields) {
        if (ObjectUtils.allNotNull(fields)) {
            return EFetchStatus.FULLY_FETCHED;
        }
        else {
            return EFetchStatus.PARTLY_FETCHED;
        }
    }

    public static boolean needsFetching(ETipStatus status) {
        if (status == null) {
            return true;
        }
        switch (status) {
            case NOBET:
            case WON:
            case LOST:
            case DNB:
                return false;

            case UNKNOWN:
            case OPEN:
            default:
                return true;
        }
    }
}
