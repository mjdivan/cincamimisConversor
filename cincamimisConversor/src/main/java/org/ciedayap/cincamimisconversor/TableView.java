/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

/**
 * This interface define the set of operations for implementing the view as a table, 
 * independently of the background implementation.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public interface TableView {
    /**
     * It is responsible for returning the first row related to the table
     * @return a Tuple instance associated with the first row in the table. This
     * method could return null when the table is empty.
     * @see org.ciedayap.cincamimisconversor.Tuple
     */
    public Tuple getFirst();
    /**
     * It returns the Tuple instance in the current position, moving the cursor
     * to the next row
     * @return The tuple located in the current row when it is present, null otherwise.
     */
    public Tuple next();
    /**
     * It returns the current row related to the Table.
     * @return The current row associated with the file
     */
    public Integer currentRow();
    /**
     * It is responsible for moving the cursor to the given index of the table
     * @param index The wished index for positioning the cursor
     * @return TRUE if the positioning was possible, FALSE otherwise.
     */
    public boolean moveCursor(int index);
    /**
     * It is responsible for returning the Tuple instance located in a given index.
     * @param index The row index in the table
     * @return The Tuple instance located in the given index, null otherwise.
     */
    public Tuple getRowAt(Integer index);
    /**
     * It returns the quantity of rows in the table
     * @return the quantity of rows in the table
     */
    public Integer getRowCount();
    /**
     * It returns the data associated with the column indicated in the index
     * @param index The index related to a given column
     * @return The data related to a given column, null otherwise.
     */
    public ColumnarData getColumnData(int index);
    /**
     * It returns the column metadata for a given column index
     * @param index The index related to a column
     * @return an Attribute instance with the column metadata, null otherwise.
     */
    public Attribute getColumnMetadata(int index);
    /**
     * It returns the set of column indexs which match with the given name.
     * @param name The name to be analyzed in the column names related to the table
     * @return An array of Long with the column index associated with the columns
     * which satisfy the column name, null otherwise.
     */
    public Integer[] getColumnIndex(String name); 
    /**
     * It adds a Tuple at the end of the table
     * @param t The tuple to be added
     * @return TRUE if the tuple was added, false otherwise
     */
    public boolean addTuple(Tuple t);
    /**
     * It removes a tuple located in the given index
     * @param index The index associated with the file to be removed
     * @return TRUE if the row(tuple was succesfully removed, false otherwise
     */
    public boolean removeAt(int index);
    /**
     * It removes all the rows in the table. Next of this operation, the table
     * will be empty.
     * @return TRUE if the table was cleaned, false otherwise.
     */
    public boolean removeAll();
}
