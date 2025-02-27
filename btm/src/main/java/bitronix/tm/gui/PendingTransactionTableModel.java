/*
 * Bitronix Transaction Manager
 *
 * Copyright (c) 2010, Bitronix Software.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA 02110-1301 USA
 */
package bitronix.tm.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.journal.TransactionLogRecord;
import bitronix.tm.utils.Decoder;
import jakarta.transaction.Status;

/**
 * <p></p>
 *
 * @author lorban
 */
public class PendingTransactionTableModel extends TransactionTableModel {

    private final static Logger log = LoggerFactory.getLogger(PendingTransactionTableModel.class);

    public PendingTransactionTableModel(File filename) {
        try {
            readFullTransactionLog(filename);
        } catch (Exception ex) {
            log.error("corrupted log file", ex);
        }
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public int getRowCount() {
        return tLogs.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TransactionLogRecord tlog = (TransactionLogRecord) tLogs.get(rowIndex);
        return switch (columnIndex) {
          case 0 -> Decoder.decodeStatus(tlog.getStatus());
          case 1 -> "" + tlog.getRecordLength();
          case 2 -> "" + tlog.getHeaderLength();
          case 3 -> "" + tlog.getTime();
          case 4 -> "" + tlog.getSequenceNumber();
          case 5 -> "" + tlog.getCrc32();
          case 6 -> "" + tlog.getUniqueNames().size();
          case 7 -> tlog.getGtrid().toString();
          default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public String getColumnName(int columnIndex) {
        return switch (columnIndex) {
          case 0 -> "Record Status";
          case 1 -> "Record length";
          case 2 -> "Header length";
          case 3 -> "Record time";
          case 4 -> "Record sequence number";
          case 5 -> "CRC";
          case 6 -> "Resources";
          case 7 -> "GTRID";
          default -> null;
        };
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }


    private Map pendingTLogs = new HashMap();

    @Override
    protected void readFullTransactionLog(File filename) throws IOException {
        super.readFullTransactionLog(filename);
        pendingTLogs.clear();
    }

    @Override
    public boolean acceptLog(TransactionLogRecord tlog) {
        if (tlog.getStatus() == Status.STATUS_COMMITTING) {
            pendingTLogs.put(tlog.getGtrid(), tlog);
            return true;
        }
        if (tlog.getStatus() == Status.STATUS_COMMITTED  ||  tlog.getStatus() == Status.STATUS_ROLLEDBACK  &&  pendingTLogs.containsKey(tlog.getGtrid().toString())) {
            tLogs.remove(pendingTLogs.get(tlog.getGtrid()));
        }
        return false;
    }

    @Override
    public TransactionLogRecord getRow(int row) {
        return (TransactionLogRecord) tLogs.get(row);
    }
}
