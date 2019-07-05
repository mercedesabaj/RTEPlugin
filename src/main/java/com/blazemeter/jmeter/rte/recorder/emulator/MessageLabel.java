package com.blazemeter.jmeter.rte.recorder.emulator;

import com.helger.commons.annotation.VisibleForTesting;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;

public class MessageLabel extends JLabel {

  private static final int TIMEOUT_MILLIS = 4500;
  private final ScheduledExecutorService messageExecutor;
  private ScheduledFuture future;

  public MessageLabel() {
    this(Executors.newSingleThreadScheduledExecutor());
  }

  @VisibleForTesting
  public MessageLabel(ScheduledExecutorService executor) {
    setVisible(false);
    messageExecutor = executor;
  }

  public synchronized void showMessage(String message) {
    if (future != null) {
      future.cancel(true);
      setVisible(false);
    }
    setText(message);
    setVisible(true);
    future = messageExecutor
        .schedule(() -> setVisible(false), TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
  }

  public void shutdown() {
    messageExecutor.shutdown();
  }

}
