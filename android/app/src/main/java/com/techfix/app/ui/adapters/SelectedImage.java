package com.techfix.app.ui.adapters;

import android.net.Uri;

public class SelectedImage {
    public enum State {
        PENDING,
        UPLOADING,
        UPLOADED,
        FAILED
    }

    private final Uri uri;
    private State state;

    public SelectedImage(Uri uri) {
        this.uri = uri;
        this.state = State.PENDING;
    }

    public Uri getUri() {
        return uri;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
