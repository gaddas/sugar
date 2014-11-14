package com.example;

import java.util.Calendar;

import com.orm.dsl.Column;
import com.orm.dsl.Table;

@Table(name = "Note")
public class Note {

	protected Long id = null;
	
    @Column(name = "noteId", unique = true, notNull = true)
    private int noteId;

    private String title;
    private String description;

	private Integer testIntegerObj;
	private Integer testIntegerObjNull;
	private int testInteger;
	private Float testFloatObj;
	private float testFloat;
	private Boolean testBooleanObj;
	private boolean testBoolean;
	private Double testDoubleObj;
	private double testDouble;
	private Byte testByteObj;
	private byte testByte;
	private Long testLongObj;
	private long testLong;
	private byte[] testBytes;
	private Calendar testDate;
	private Calendar testDateNull;
	private char testChar;
	private Character testCharObj;
	private Character testCharObjNull;

	private Tag tag;

	public Note() {
	}

	public Note(int noteId, String title, String description, Tag tag) {
		this.noteId = noteId;
		this.title = title;
		this.description = description;
		this.tag = tag;

		testIntegerObj = 2;
		testIntegerObjNull = null;
		testInteger = 3;
		testFloatObj = 4.0f;
		testFloat = 5.0f;
		testBooleanObj = false;
		testBoolean = true;
		testDoubleObj = 40.5;
		testDouble = 24.4;
		testByteObj = 124;
		testByte = 125;
		testLongObj = 13545L;
		testLong = 13546L;
		testBytes = new byte[] { 1, 2, 3, 4 };
		testDate = Calendar.getInstance();
		testDateNull = null;
		testChar = 'c';
		testCharObj = 'c';
		testCharObjNull = null;
	}

	public boolean Validate() {
		if (testIntegerObj != 2)
			return false;
		if (testIntegerObjNull != null)
			return false;
		if (testInteger != 3)
			return false;
		if (testFloatObj != 4.0f)
			return false;
		if (testFloat != 5.0f)
			return false;
		if (testBooleanObj != false)
			return false;
		if (testBoolean != true)
			return false;
		if (testDoubleObj != 40.5)
			return false;
		if (testDouble != 24.4)
			return false;
		if (testByteObj != 124)
			return false;
		if (testByte != 125)
			return false;
		if (testLongObj != 13545L)
			return false;
		if (testLong != 13546L)
			return false;
		if (testBytes == null)
			return false;
		if (testBytes[0] != 1)
			return false;
		if (testBytes[1] != 2)
			return false;
		if (testBytes[2] != 3)
			return false;
		if (testBytes[3] != 4)
			return false;
		if (testDate == null)
			return false;
		if (testDateNull != null)
			return false;
		if (testChar != 'c')
			return false;
		if (testCharObj != 'c')
			return false;
		if (testCharObjNull != null)
			return false;
		
		return true;
	}

	public int getNoteId() {
		return noteId;
	}

	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Tag getTag() {
		return tag;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title + "noteId: " + noteId + " - " + tag;
	}
}
