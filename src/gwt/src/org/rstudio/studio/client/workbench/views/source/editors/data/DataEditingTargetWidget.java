/*
 * DataEditingTargetWidget.java
 *
 * Copyright (C) 2009-15 by RStudio, Inc.
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

package org.rstudio.studio.client.workbench.views.source.editors.data;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;

import org.rstudio.core.client.StringUtil;
import org.rstudio.core.client.dom.IFrameElementEx;
import org.rstudio.core.client.dom.WindowEx;
import org.rstudio.core.client.theme.ThemeColors;
import org.rstudio.core.client.widget.RStudioFrame;
import org.rstudio.core.client.widget.Toolbar;
import org.rstudio.studio.client.common.AutoGlassPanel;
import org.rstudio.studio.client.dataviewer.DataTable;
import org.rstudio.studio.client.workbench.commands.Commands;
import org.rstudio.studio.client.workbench.views.source.PanelWithToolbars;
import org.rstudio.studio.client.workbench.views.source.editors.EditingTargetToolbar;
import org.rstudio.studio.client.workbench.views.source.editors.urlcontent.UrlContentEditingTarget;
import org.rstudio.studio.client.workbench.views.source.model.DataItem;

public class DataEditingTargetWidget extends Composite
   implements UrlContentEditingTarget.Display, 
              DataTable.Host
{
   interface Resources extends ClientBundle
   {
      @Source("DataEditingTargetWidget.css")
      Styles styles();
   }
   private static Resources resources = GWT.create(Resources.class);

   public interface Styles extends CssResource
   {
      String description();

      String statusBar();
      String statusBarDisplayed();
      String statusBarOmitted();
   }

   static
   {
      resources.styles().ensureInjected();
   }

   private String getCustomStyle()
   {
      return
         ".rstudio-themes-flat.rstudio-themes-default #rsGridData_info,\n" +
         ".rstudio-themes-flat.rstudio-themes-default table.dataTable thead th,\n" +
         ".rstudio-themes-flat.rstudio-themes-default table.dataTable thead td,\n" +
         ".rstudio-themes-flat.rstudio-themes-default table.dataTable tbody td {\n" +
         "   border-color: " + ThemeColors.getDefaultBorder() + ";\n" +
         "}\n" +
         "\n" +
         ".rstudio-themes-flat.rstudio-themes-dark-grey #rsGridData_info,\n" +
         ".rstudio-themes-flat.rstudio-themes-dark-grey table.dataTable thead th,\n" +
         ".rstudio-themes-flat.rstudio-themes-dark-grey table.dataTable thead td,\n" +
         ".rstudio-themes-flat.rstudio-themes-dark-grey table.dataTable tbody td {\n" +
         "   border-color: " + ThemeColors.getDarkGreyBorder() + ";\n" +
         "}\n" +
         "\n" +
         ".rstudio-themes-flat.rstudio-themes-alternate #rsGridData_info,\n" +
         ".rstudio-themes-flat.rstudio-themes-alternate table.dataTable thead th,\n" +
         ".rstudio-themes-flat.rstudio-themes-alternate table.dataTable thead td,\n" +
         ".rstudio-themes-flat.rstudio-themes-alternate table.dataTable tbody td {\n" +
         "   border-color: " + ThemeColors.getAlternateBorder() + ";\n" +
         "}\n" +
         "\n" +
         ".rstudio-themes-flat.rstudio-themes-default #rsGridData_info,\n" +
         ".rstudio-themes-flat.rstudio-themes-default th,\n" +
         ".rstudio-themes-flat.rstudio-themes-default td.first-child,\n" +
         ".rstudio-themes-flat.rstudio-themes-default table.dataTable thead td {\n" +
         "   background-color: " + ThemeColors.getDefaultBackground() + ";\n" +
         "}\n" +
         "\n" +
         ".rstudio-themes-flat.rstudio-themes-dark-grey #rsGridData_info,\n" +
         ".rstudio-themes-flat.rstudio-themes-dark-grey th,\n" +
         ".rstudio-themes-flat.rstudio-themes-dark-grey td.first-child,\n" +
         ".rstudio-themes-flat.rstudio-themes-dark-grey table.dataTable thead th {\n" +
         "   background-color: " + ThemeColors.getDarkGreyBackground() + ";\n" +
         "}\n" +
         "\n" +
         ".rstudio-themes-flat.rstudio-themes-alternate #rsGridData_info,\n" +
         ".rstudio-themes-flat.rstudio-themes-alternate th,\n" +
         ".rstudio-themes-flat.rstudio-themes-alternate td.first-child,\n" +
         ".rstudio-themes-flat.rstudio-themes-alternate table.dataTable thead th {\n" +
         "   background-color: " + ThemeColors.getAlternateBackground() + ";\n" +
         "}\n" +
         "\n";
   }

   public DataEditingTargetWidget(Commands commands, DataItem dataItem)
   {
      Styles styles = resources.styles();

      commands_ = commands;

      frame_ = new RStudioFrame(dataItem.getContentUrl());
      frame_.setAceThemeAndCustomStyle(getCustomStyle());
      frame_.setSize("100%", "100%");
      table_ = new DataTable(this);

      Widget mainWidget;

      if (dataItem.getDisplayedObservations() != dataItem.getTotalObservations())
      {
         FlowPanel statusBar = new FlowPanel();
         statusBar.setStylePrimaryName(styles.statusBar());
         statusBar.setSize("100%", "100%");
         Label label1 = new Label(
               "Displayed "
               + StringUtil.formatGeneralNumber(dataItem.getDisplayedObservations())
               + " rows of "
               + StringUtil.formatGeneralNumber(dataItem.getTotalObservations()));
         int omitted = dataItem.getTotalObservations()
                       - dataItem.getDisplayedObservations();
         Label label2 = new Label("(" +
                                  StringUtil.formatGeneralNumber(omitted) +
                                  " omitted)");

         label1.addStyleName(styles.statusBarDisplayed());
         label2.addStyleName(styles.statusBarOmitted());

         statusBar.add(label1);
         statusBar.add(label2);

         DockLayoutPanel dockPanel = new DockLayoutPanel(Unit.PX);
         dockPanel.addSouth(statusBar, 20);
         dockPanel.add(new AutoGlassPanel(frame_));
         dockPanel.setSize("100%", "100%");
         mainWidget = dockPanel;
      }
      else
      {
         mainWidget = new AutoGlassPanel(frame_);
      }
      

      PanelWithToolbars panel = new PanelWithToolbars(
            createToolbar(dataItem, styles), 
            mainWidget);

      initWidget(panel);
   }

   private Toolbar createToolbar(DataItem dataItem, Styles styles)
   {

      Toolbar toolbar = new EditingTargetToolbar(commands_, true);
      table_.initToolbar(toolbar, dataItem.isPreview());

      return toolbar;
   }
   
   private WindowEx getWindow()
   {
      IFrameElementEx frameEl = (IFrameElementEx) frame_.getElement().cast();
      return frameEl.getContentWindow();
   }

   public void print()
   {
      getWindow().print();
   }
   
   public void setFilterUIVisible(boolean visible)
   {
      if (table_ != null)
         table_.setFilterUIVisible(visible);
   }
   
   public void refreshData(boolean structureChanged, boolean sizeChanged)
   {
      if (table_ != null)
         table_.refreshData(structureChanged, sizeChanged);
   }
   
   public void onActivate()
   {
      if (table_ != null)
         table_.onActivate();
   }
   
   public void onDeactivate()
   {
      if (table_ != null)
         table_.onDeactivate();
   }
   
   @Override
   public RStudioFrame getDataTableFrame()
   {
      return frame_;
   }

   public Widget asWidget()
   {
      return this;
   }

   private final Commands commands_;
   private RStudioFrame frame_;
   private DataTable table_;
}
