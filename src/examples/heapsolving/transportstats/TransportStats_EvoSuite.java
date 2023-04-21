/*
 * Created on May 19, 2005
 * Created by Alon Rohter
 * Copyright (C) 2005, 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package heapsolving.transportstats;

import java.util.HashSet;
import java.util.Set;

public class TransportStats_EvoSuite {

    // private static final int PRINT_INTERVAL = 60 * 1000;
    private static final int GRANULARITY = 10; // bytes

    private TreeMapIntLong_EvoSuite read_sizes = new TreeMapIntLong_EvoSuite();
    private TreeMapIntLong_EvoSuite write_sizes = new TreeMapIntLong_EvoSuite();

    private long total_reads = 0;
    private long total_writes = 0;

    public TransportStats_EvoSuite() {
//    Timer printer = new Timer("TransportStats:Printer");
//    printer.addPeriodicEvent(
//        PRINT_INTERVAL,
//        new TimerEventPerformer() {
//          public void perform( TimerEvent ev ) {
//            printStats();
//          }
//        }
//    );
    }

    public void bytesRead(int num_bytes_read) {
        total_reads++;
        updateSizes(read_sizes, num_bytes_read);
    }

    public void bytesWritten(int num_bytes_written) {
        total_writes++;
        updateSizes(write_sizes, num_bytes_written);
    }

    private void updateSizes(TreeMapIntLong_EvoSuite io_sizes, int num_bytes) {
        int size_key;

        if (num_bytes == 0) {
            size_key = 0;
        } else {
            size_key = (num_bytes / GRANULARITY) + 1;
        }

        Long count = (Long) io_sizes.get(size_key);

        if (count == null) {
            io_sizes.put(size_key, new Long(1));
        } else {
            io_sizes.put(size_key, new Long(count.longValue() + 1));
        }
    }

    public boolean repOKSymSolve() {
        if (read_sizes == null || write_sizes == null)
            return false;
        if (read_sizes == write_sizes)
            return false;
        Set<TreeMapIntLong_EvoSuite.Entry> visited = new HashSet<>();
        if (!read_sizes.isBinTreeWithParentReferences(visited))
            return false;
        if (!write_sizes.isBinTreeWithParentReferences(visited))
            return false;
        if (!read_sizes.isWellColored())
            return false;
        if (!write_sizes.isWellColored())
            return false;
        return true;
    }

    public boolean repOKSymbolicExecution() {
        if (!read_sizes.isSorted())
            return false;
        if (!write_sizes.isSorted())
            return false;
        return true;
    }

    public boolean repOKComplete() {
        return repOKSymSolve() && repOKSymbolicExecution();
    }

//  private void printStats() {
//    System.out.println( "\n------------------------------" );
//    System.out.println( "***** TCP SOCKET READ SIZE STATS *****" );
//    printSizes( read_sizes, total_reads );
//
//    System.out.println( "\n***** TCP SOCKET WRITE SIZE STATS *****" );
//    printSizes( write_sizes, total_writes );
//    System.out.println( "------------------------------" );
//  }
//
//
//
//  private void printSizes( TreeMapIntLong size_map, long num_total ) {
//    int prev_high = 1;
//
//    for( Iterator it = size_map.entrySet().iterator(); it.hasNext(); ) {
//      Map.Entry entry = (Map.Entry)it.next();
//      int key = ((Integer)entry.getKey()).intValue();
//      long count = ((Long)entry.getValue()).longValue();
//
//      long percentage = (count *100) / num_total;
//
//      if( key == 0 ) {
//        if( percentage > 3 ) {
//          System.out.println( "[0 bytes]= x" +percentage+ "%" );
//        }
//      }
//      else {
//        int high = key * GRANULARITY;
//
//        if( percentage > 3 ) {
//          System.out.println( "[" +prev_high+ "-" +(high -1)+ " bytes]= x" +percentage+ "%" );
//        }
//
//        prev_high = high;
//      }
//    }
//  }

}
