import static org.junit.Assert.*;

import org.junit.Test;

public class TextBuddySequelTest {
	
	TextBuddySequel textBuddySecond = new TextBuddySequel();
	
	@Test
	public void test(){
		
		testAdd();
		testDelete();
		testClear();
		testSort();
		testSearch();
	}
	
	private void testAdd(){
		assertEquals("", "added to textFile.txt: \"35xxxv\"\n", textBuddySecond.executeRun("add 35xxxv"));
		assertEquals("", "added to textFile.txt: \"take me to the top\"\n", textBuddySecond.executeRun("add take me to the top"));
		assertEquals("", "added to textFile.txt: \"cry out\"\n", textBuddySecond.executeRun("add cry out"));
		assertEquals("", "added to textFile.txt: \"mighty long fall\"\n", textBuddySecond.executeRun("add mighty long fall"));
	}
	
	private void testDelete(){
		assertEquals("", "deleted from textFile.txt: \"35xxxv\"\n", textBuddySecond.executeRun("delete 1"));
		assertEquals("", "1. take me to the top\n2. cry out\n3. mighty long fall\n", textBuddySecond.executeRun("display"));
	}
	
	private void testClear(){
		assertEquals("", "added to textFile.txt: \"35xxxv\"\n", textBuddySecond.executeRun("add 35xxxv"));
		assertEquals("", "added to textFile.txt: \"take me to the top\"\n", textBuddySecond.executeRun("add take me to the top"));
		assertEquals("", "all content deleted from textFile.txt\n", textBuddySecond.executeRun("clear"));
		assertEquals("", "textFile.txt is empty\n", textBuddySecond.executeRun("display"));
	}
	
	private void testSort(){
		//textBuddySecond.executeRun("display");
		assertEquals("", "added to textFile.txt: \"35xxxv\"\n", textBuddySecond.executeRun("add 35xxxv"));
		assertEquals("", "added to textFile.txt: \"take me to the top\"\n", textBuddySecond.executeRun("add take me to the top"));
		assertEquals("", "added to textFile.txt: \"cry out\"\n", textBuddySecond.executeRun("add cry out"));
		assertEquals("", "added to textFile.txt: \"mighty long fall\"\n", textBuddySecond.executeRun("add mighty long fall"));
		assertEquals("", "all texts have been sorted alphabetically\n", textBuddySecond.executeRun("sort"));
		
	}
	
	private void testSearch(){
		//textBuddySecond.executeRun("display");
		assertEquals("", "textList does not contain the keyword w\n", textBuddySecond.executeRun("search w"));
		assertEquals("", "1. 35xxxv\n", textBuddySecond.executeRun("search 3"));
		assertEquals("", "1. cry out\n", textBuddySecond.executeRun("search cry"));
		assertEquals("", "1. cry out\n2. mighty long fall\n3. take me to the top\n", textBuddySecond.executeRun("search t"));
	}
}
