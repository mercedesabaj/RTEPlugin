package com.blazemeter.jmeter.rte.sampler.gui;

import com.blazemeter.jmeter.rte.core.Action;
import com.blazemeter.jmeter.rte.sampler.RTESampler;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

public class RTESamplerPanel extends JPanel {

  private static final long serialVersionUID = 4739160923223292835L;
  private static final int INDEX_WIDTH = 30;
  private static final int TIME_WIDTH = 60;

  private final JPanel requestPanel;
  private CoordInputPanel payloadPanel;
  private ButtonGroup actionsGroup = new ButtonGroup();
  private Map<Action, JRadioButton> actions = new HashMap<>();
  private JCheckBox disconnect = new JCheckBox("Disconnect?");
  private JCheckBox justConnect = new JCheckBox("Just connect");
  private JPanel waitSyncPanel;
  private JCheckBox waitSync = new JCheckBox("Sync?");
  private JTextField waitSyncTimeout = new JTextField();
  private JPanel waitCursorPanel;
  private JCheckBox waitCursor = new JCheckBox("Cursor?");
  private JTextField waitCursorRow = new JTextField();
  private JTextField waitCursorColumn = new JTextField();
  private JTextField waitCursorTimeout = new JTextField();
  private JPanel waitSilentPanel;
  private JCheckBox waitSilent = new JCheckBox("Silent?");
  private JTextField waitSilentTime = new JTextField();
  private JTextField waitSilentTimeout = new JTextField();
  private JPanel waitTextPanel;
  private JCheckBox waitText = new JCheckBox("Text?");
  private JTextField waitTextRegex = new JTextField();
  private JTextField waitTextTimeout = new JTextField();
  private JTextField waitTextAreaTop = new JTextField();
  private JTextField waitTextAreaLeft = new JTextField();
  private JTextField waitTextAreaBottom = new JTextField();
  private JTextField waitTextAreaRight = new JTextField();

  public RTESamplerPanel() {
    GroupLayout layout = new GroupLayout(this);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateGaps(true);
    this.setLayout(layout);

    requestPanel = buildRequestPanel();
    JPanel waitPanel = buildWaitsPanel();

    layout.setHorizontalGroup(layout.createParallelGroup()
        .addComponent(requestPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
            Short.MAX_VALUE)
        .addComponent(waitPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
            Short.MAX_VALUE));
    layout.setVerticalGroup(layout.createSequentialGroup()
        .addComponent(requestPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
            GroupLayout.DEFAULT_SIZE)
        .addComponent(waitPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
            GroupLayout.DEFAULT_SIZE)
    );
  }

  private JPanel buildRequestPanel() {
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createTitledBorder("RTE Message"));
    GroupLayout layout = new GroupLayout(panel);
    layout.setAutoCreateContainerGaps(true);
    panel.setLayout(layout);

    JLabel payloadLabel = new JLabel("Payload: ");
    payloadPanel = new CoordInputPanel();
    JPanel actionsPanel = buildActionsPanel();

    justConnect.addItemListener(e -> {
      updateJustConnect(e.getStateChange() == ItemEvent.SELECTED);
      validate();
      repaint();
    });

    layout.setHorizontalGroup(layout.createParallelGroup()
        .addComponent(payloadLabel)
        .addComponent(payloadPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
            Short.MAX_VALUE)
        .addComponent(actionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
            Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addComponent(disconnect)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(justConnect)));

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(payloadLabel)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(payloadPanel, GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(actionsPanel, GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup()
            .addComponent(disconnect)
            .addComponent(justConnect)));

    return panel;
  }

  private void updateJustConnect(boolean checked) {
    setEnabled(requestPanel, !checked);
    disconnect.setEnabled(true);
    justConnect.setEnabled(true);
  }

  private void setEnabled(Component component, boolean enabled) {
    component.setEnabled(enabled);
    if (component instanceof Container) {
      for (Component child : ((Container) component).getComponents()) {
        setEnabled(child, enabled);
      }
    }
  }

  private JPanel buildActionsPanel() {
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createTitledBorder("Actions"));
    panel.setLayout(new GridLayout(0, 12));

    Arrays.stream(Action.values()).forEach(t -> {
      JRadioButton r = new JRadioButton(t.toString());
      r.setActionCommand(t.toString());
      panel.add(r);
      actions.put(t, r);
      actionsGroup.add(r);
    });

    return panel;
  }

  private JPanel buildWaitsPanel() {
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createTitledBorder("Wait for:"));
    GroupLayout layout = new GroupLayout(panel);
    layout.setAutoCreateContainerGaps(true);
    layout.setAutoCreateGaps(true);
    panel.setLayout(layout);

    waitSyncPanel = buildWaitSyncPanel();
    waitCursorPanel = buildWaitCursorPanel();
    waitSilentPanel = buildWaitSilentPanel();
    waitTextPanel = buildWaitTextPanel();

    layout.setHorizontalGroup(layout.createParallelGroup()
        .addComponent(waitSyncPanel)
        .addComponent(waitCursorPanel)
        .addComponent(waitSilentPanel)
        .addComponent(waitTextPanel));
    layout.setVerticalGroup(layout.createSequentialGroup()
        .addComponent(waitSyncPanel)
        .addComponent(waitCursorPanel)
        .addComponent(waitSilentPanel)
        .addComponent(waitTextPanel));

    return panel;
  }

  private JPanel buildWaitSyncPanel() {
    JPanel panel = new JPanel();
    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);

    waitSync.addItemListener(e -> {
      updateWait(waitSync, panel, e.getStateChange() == ItemEvent.SELECTED);
      validate();
      repaint();
    });

    JLabel timeoutLabel = new JLabel("Timeout (millis): ");
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addComponent(waitSync)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(timeoutLabel)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(waitSyncTimeout, GroupLayout.PREFERRED_SIZE, TIME_WIDTH,
            GroupLayout.PREFERRED_SIZE));
    layout.setVerticalGroup(layout.createParallelGroup(Alignment.BASELINE)
        .addComponent(waitSync)
        .addComponent(timeoutLabel)
        .addComponent(waitSyncTimeout));

    return panel;
  }

  private void updateWait(JCheckBox waitCheck, JPanel panel, boolean checked) {
    setEnabled(panel, checked);
    waitCheck.setEnabled(true);
  }

  private JPanel buildWaitCursorPanel() {
    JPanel panel = new JPanel();
    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);

    waitCursor.addItemListener(e -> {
      updateWait(waitCursor, panel, e.getStateChange() == ItemEvent.SELECTED);
      validate();
      repaint();
    });

    JLabel rowLabel = new JLabel("Row: ");
    JLabel columnLabel = new JLabel("Column: ");
    JLabel timeoutLabel = new JLabel("Timeout (millis): ");
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addComponent(waitCursor)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(rowLabel)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(waitCursorRow, GroupLayout.PREFERRED_SIZE, INDEX_WIDTH,
            GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(columnLabel)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(waitCursorColumn, GroupLayout.PREFERRED_SIZE, INDEX_WIDTH,
            GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(timeoutLabel)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(waitCursorTimeout, GroupLayout.PREFERRED_SIZE, TIME_WIDTH,
            GroupLayout.PREFERRED_SIZE));
    layout.setVerticalGroup(layout.createParallelGroup(Alignment.BASELINE)
        .addComponent(waitCursor)
        .addComponent(rowLabel)
        .addComponent(waitCursorRow)
        .addComponent(columnLabel)
        .addComponent(waitCursorColumn)
        .addComponent(timeoutLabel)
        .addComponent(waitCursorTimeout));

    return panel;
  }

  private JPanel buildWaitSilentPanel() {
    JPanel panel = new JPanel();
    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);

    waitSilent.addItemListener(e -> {
      updateWait(waitSilent, panel, e.getStateChange() == ItemEvent.SELECTED);
      validate();
      repaint();
    });

    JLabel timeLabel = new JLabel("Wait for silent (millis): ");
    JLabel timeoutLabel = new JLabel("Timeout (millis): ");
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addComponent(waitSilent)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(timeLabel)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(waitSilentTime, GroupLayout.PREFERRED_SIZE, TIME_WIDTH,
            GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(timeoutLabel)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(waitSilentTimeout, GroupLayout.PREFERRED_SIZE, TIME_WIDTH,
            GroupLayout.PREFERRED_SIZE));
    layout.setVerticalGroup(layout.createParallelGroup(Alignment.BASELINE)
        .addComponent(waitSilent)
        .addComponent(timeoutLabel)
        .addComponent(waitSilentTimeout)
        .addComponent(timeLabel)
        .addComponent(waitSilentTime));
    return panel;
  }

  private JPanel buildWaitTextPanel() {
    JPanel panel = new JPanel();
    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);

    waitText.addItemListener(e -> {
      updateWait(waitText, panel, e.getStateChange() == ItemEvent.SELECTED);
      validate();
      repaint();
    });

    JLabel regexLabel = new JLabel("Regex: ");
    JLabel timeoutLabel = new JLabel("Timeout (millis): ");
    JPanel searchAreaPanel = buildSearchAreaPanel();
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addComponent(waitText)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(regexLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(waitTextRegex, GroupLayout.PREFERRED_SIZE, 200,
                    GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(timeoutLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(waitTextTimeout, GroupLayout.PREFERRED_SIZE, TIME_WIDTH,
                    GroupLayout.PREFERRED_SIZE))
            .addComponent(searchAreaPanel))
    );
    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(waitText)
            .addComponent(regexLabel)
            .addComponent(waitTextRegex)
            .addComponent(timeoutLabel)
            .addComponent(waitTextTimeout))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(searchAreaPanel));

    return panel;
  }

  private JPanel buildSearchAreaPanel() {
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createTitledBorder("Search area: "));
    GroupLayout layout = new GroupLayout(panel);
    layout.setAutoCreateContainerGaps(true);
    panel.setLayout(layout);

    JLabel topLabel = new JLabel("Top row: ");
    JLabel leftLabel = new JLabel("Left column: ");
    JLabel bottomLabel = new JLabel("Bottom row: ");
    JLabel rightLabel = new JLabel("Right column: ");
    layout.setHorizontalGroup(
        layout.createSequentialGroup()
            .addComponent(leftLabel)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(waitTextAreaLeft, GroupLayout.PREFERRED_SIZE, INDEX_WIDTH,
                GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(topLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(waitTextAreaTop, GroupLayout.PREFERRED_SIZE, INDEX_WIDTH,
                        GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(bottomLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(waitTextAreaBottom, GroupLayout.PREFERRED_SIZE, INDEX_WIDTH,
                        GroupLayout.PREFERRED_SIZE)
                )
            )
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(rightLabel)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(waitTextAreaRight, GroupLayout.PREFERRED_SIZE, INDEX_WIDTH,
                GroupLayout.PREFERRED_SIZE));
    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.BASELINE, false)
            .addComponent(topLabel)
            .addComponent(waitTextAreaTop))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(Alignment.BASELINE, false)
            .addComponent(leftLabel)
            .addComponent(waitTextAreaLeft)
            .addComponent(rightLabel)
            .addComponent(waitTextAreaRight))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(Alignment.BASELINE, false)
            .addComponent(bottomLabel)
            .addComponent(waitTextAreaBottom)));

    return panel;
  }

  public void resetFields() {
    payloadPanel.clear();
  }

  public CoordInputPanel getPayload() {
    return this.payloadPanel;
  }

  public Action getAction() {
    String action = actionsGroup.getSelection().getActionCommand();
    return Action.valueOf(action);
  }

  public void setAction(Action action) {
    if (actions.containsKey(action)) {
      actions.get(action).setSelected(true);
    } else {
      actions.get(RTESampler.DEFAULT_ACTION).setSelected(true);
    }
  }

  public boolean getDisconnect() {
    return this.disconnect.isSelected();
  }

  public void setDisconnect(boolean disconnect) {
    this.disconnect.setSelected(disconnect);
  }

  public boolean getJustConnect() {
    return justConnect.isSelected();
  }

  public void setJustConnect(boolean justConnect) {
    this.justConnect.setSelected(justConnect);
    updateJustConnect(justConnect);
  }

  public boolean getWaitSync() {
    return this.waitSync.isSelected();
  }

  public void setWaitSync(boolean waitSync) {
    this.waitSync.setSelected(waitSync);
    updateWait(this.waitSync, waitSyncPanel, waitSync);
  }

  public String getWaitSyncTimeout() {
    return this.waitSyncTimeout.getText();
  }

  public void setWaitSyncTimeout(String waitSyncTimeout) {
    this.waitSyncTimeout.setText(waitSyncTimeout);
  }

  public boolean getWaitCursor() {
    return this.waitCursor.isSelected();
  }

  public void setWaitCursor(boolean waitCursor) {
    this.waitCursor.setSelected(waitCursor);
    updateWait(this.waitCursor, waitCursorPanel, waitCursor);
  }

  public String getWaitCursorRow() {
    return this.waitCursorRow.getText();
  }

  public void setWaitCursorRow(String waitCursorRow) {
    this.waitCursorRow.setText(waitCursorRow);
  }

  public String getWaitCursorColumn() {
    return this.waitCursorColumn.getText();
  }

  public void setWaitCursorColumn(String waitCursorColumn) {
    this.waitCursorColumn.setText(waitCursorColumn);
  }

  public String getWaitCursorTimeout() {
    return this.waitCursorTimeout.getText();
  }

  public void setWaitCursorTimeout(String waitCursorTimeout) {
    this.waitCursorTimeout.setText(waitCursorTimeout);
  }

  public boolean getWaitSilent() {
    return this.waitSilent.isSelected();
  }

  public void setWaitSilent(boolean waitSilent) {
    this.waitSilent.setSelected(waitSilent);
    updateWait(this.waitSilent, waitSilentPanel, waitSilent);
  }

  public String getWaitSilentTime() {
    return this.waitSilentTime.getText();
  }

  public void setWaitSilentTime(String waitSilentTime) {
    this.waitSilentTime.setText(waitSilentTime);
  }

  public String getWaitSilentTimeout() {
    return this.waitSilentTimeout.getText();
  }

  public void setWaitSilentTimeout(String waitSilentTimeout) {
    this.waitSilentTimeout.setText(waitSilentTimeout);
  }

  public boolean getWaitText() {
    return this.waitText.isSelected();
  }

  public void setWaitText(boolean waitText) {
    this.waitText.setSelected(waitText);
    updateWait(this.waitText, waitTextPanel, waitText);
  }

  public String getWaitTextRegex() {
    return this.waitTextRegex.getText();
  }

  public void setWaitTextRegex(String waitTextRegex) {
    this.waitTextRegex.setText(waitTextRegex);
  }

  public String getWaitTextAreaTop() {
    return this.waitTextAreaTop.getText();
  }

  public void setWaitTextAreaTop(String row) {
    this.waitTextAreaTop.setText(row);
  }

  public String getWaitTextAreaLeft() {
    return this.waitTextAreaLeft.getText();
  }

  public void setWaitTextAreaLeft(String column) {
    this.waitTextAreaLeft.setText(column);
  }

  public String getWaitTextAreaBottom() {
    return this.waitTextAreaBottom.getText();
  }

  public void setWaitTextAreaBottom(String row) {
    this.waitTextAreaBottom.setText(row);
  }

  public String getWaitTextAreaRight() {
    return this.waitTextAreaRight.getText();
  }

  public void setWaitTextAreaRight(String column) {
    this.waitTextAreaRight.setText(column);
  }

  public String getWaitTextTimeout() {
    return this.waitTextTimeout.getText();
  }

  public void setWaitTextTimeout(String waitTextTimeout) {
    this.waitTextTimeout.setText(waitTextTimeout);
  }

}
