package de.tuberlin.dima.minidb.io.tables;

import de.tuberlin.dima.minidb.core.DataTuple;
import de.tuberlin.dima.minidb.qexec.LowLevelPredicate;

public class G10TupleIterator implements TupleIterator {
	
	int position;
	int numCols;
	long columnBitmap;
	LowLevelPredicate[] preds;
	
	G10TablePage page;
	
	public G10TupleIterator(G10TablePage page, int numCols, long columnBitmap) {
		
		this.page = page;
		this.numCols = numCols;
		this.columnBitmap = columnBitmap;
		
		position = 0;
		
	}
	
	public G10TupleIterator(G10TablePage page, LowLevelPredicate[] preds, int numCols, long columnBitmap) {
		
		this.page = page;
		this.numCols = numCols;
		this.columnBitmap = columnBitmap;
		this.preds = preds;
		position = 0;
		
	}

	@Override
	public boolean hasNext() throws PageTupleAccessException {
		
		int numRecords = page.getNumRecordsOnPage();
		
		
		
		for (int i = position +1; i < numRecords; i++){
			
			if ((page.getTombstone(i) & 0x1) == 0) {
				
				if (preds == null)		
					return true;	
				
				if (page.getDataTuple(preds, i, columnBitmap, numCols) != null) 					
					return true;

			}
		}
		return false;
	}

	
	
	
	@Override
	public DataTuple next() throws PageTupleAccessException {
		
		int numRecords = page.getNumRecordsOnPage();
		
		for (int i = position +1; i < numRecords; i++){
			
			if ((page.getTombstone(i) & 0x1) == 0) {
				if (preds == null) {
					position = i;
					return page.getDataTuple( i, columnBitmap, numCols);
				}
				
				
				DataTuple tuple = page.getDataTuple(preds, i, columnBitmap, numCols);
				
				if (tuple != null) {
					position = i;
					return tuple;	
				}
			}
		}
		return null;
	}

}
