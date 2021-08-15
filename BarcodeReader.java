/////////////////////////////////////////////////////////
//                                                     //
// Description:                                        //
//                                                     //
//    Created an optical barcode reader that           //
//    utilizes 2d arrays and interfaces. This project  //
//    involved pattern recognition and allows the      //
//    user to enter a string that will then be         //
//    translated into a barcode image and a barcode    // 
//    image that gets translated into a string.        //
//                                                     //
/////////////////////////////////////////////////////////
public class BarcodeReader
{
   public static void main(String[] args) 
   {
      String[] sImageIn =
      {
         "                                               ",
         "                                               ",
         "                                               ",
         "     * * * * * * * * * * * * * * * * * * * * * ",
         "     *                                       * ",
         "     ****** **** ****** ******* ** *** *****   ",
         "     *     *    ****************************** ",
         "     * **    * *        **  *    * * *   *     ",
         "     *   *    *  *****    *   * *   *  **  *** ",
         "     *  **     * *** **   **  *    **  ***  *  ",
         "     ***  * **   **  *   ****    *  *  ** * ** ",
         "     *****  ***  *  * *   ** ** **  *   * *    ",
         "     ***************************************** ",  
         "                                               ",
         "                                               ",
         "                                               "
      };                  
      
      String[] sImageIn_2 =
      {
         "                                          ",
         "                                          ",
         "* * * * * * * * * * * * * * * * * * *     ",
         "*                                    *    ",
         "**** *** **   ***** ****   *********      ",
         "* ************ ************ **********    ",
         "** *      *    *  * * *         * *       ",
         "***   *  *           * **    *      **    ",
         "* ** * *  *   * * * **  *   ***   ***     ",
         "* *           **    *****  *   **   **    ",
         "****  *  * *  * **  ** *   ** *  * *      ",
         "**************************************    ",
         "                                          ",
         "                                          ",
         "                                          ",
         "                                          "
      };
     
      BarcodeImage bc = new BarcodeImage(sImageIn);
      DataMatrix dm = new DataMatrix(bc);
     
      // First secret message
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // second secret message
      bc = new BarcodeImage(sImageIn_2);
      dm.scan(bc);
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // create your own message
      dm.readText("What a great resume builder this is!");
      dm.generateImageFromText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // create your own message
      dm.readText("Test message: Osprey Corp is the best Corp");
      dm.generateImageFromText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
   }
}

interface BarcodeIO 
{
   public boolean scan(BarcodeImage bc);
   public boolean readText(String text);
   public boolean generateImageFromText();
   public boolean translateImageToText();
   public void displayTextToConsole();
   public void displayImageToConsole();
 }

class BarcodeImage implements Cloneable
{
   public static final int MAX_HEIGHT = 30;
   public static final int MAX_WIDTH = 65;
   private boolean[][] imageData;
   
   public BarcodeImage()
   {
      //Instantiate 2D array
      imageData = new boolean [MAX_HEIGHT][MAX_WIDTH];
      
      //Fill array with blanks
      for(int i = 0; i < MAX_HEIGHT; i++)
         for(int j = 0; j < MAX_WIDTH; j++)
             imageData[i][j] = false;  
   }
   
   //Translate string array into 2D boolean array
   public BarcodeImage(String[] strData)
   {
      //Helper variable to fill 2D array starting from lower left corner
      int position = MAX_HEIGHT;
      
      //Account for if string array is too small to fill 2D array by ensuring
      //that missing spots are set to blanks by default
      imageData = new boolean [MAX_HEIGHT][MAX_WIDTH];
      for(int i = 0; i < MAX_HEIGHT; i++)
         for(int j = 0; j < MAX_WIDTH; j++)
             imageData[i][j] = false;
      
      if(checkSize(strData))
      {
         //Start filling from bottom row of array
         for(int i = strData.length - 1; i >= 0; i--)
         {
            //Decrease helper variable to fill 2D array properly
            position--;
            
            //Start filling from beginning of columns
            for(int j = 0; j < strData[i].length(); j++)
            {
               if(strData[i].charAt(j) == ' ')
                  imageData[position][j] = false;
               else
                  imageData[position][j] = true;
            }
         } 
      }
   }
   
   //Return value of pixel if valid
   public boolean getPixel(int row, int col)
   {
      if(row < MAX_HEIGHT && row >= 0 && col >= 0 && col < MAX_WIDTH)
         return imageData[row][col];
      else 
         return false;
   }
   
   //Set value of pixel if valid
   public boolean setPixel(int row, int col, boolean value)
   {
      if(row < MAX_HEIGHT && row >= 0 && col >= 0 && col < MAX_WIDTH)
      {
         imageData[row][col] = value;
         return true;
      }
      else
         return false;
   }
   
   //Make and return a copy of a BarcodeImage object
   public Object clone() throws CloneNotSupportedException
   {
      return (BarcodeImage)super.clone();
   }
   
   //Displays the barcode on the screen
   public void displayToConsole()
   {
      for(int i = 0; i < MAX_HEIGHT; i++)
      {
         if(i != 0)
         {
            System.out.print("\n");
         }
         for(int j = 0; j < MAX_WIDTH; j++)
         {
            if(imageData[i][j] == false)
               System.out.print(DataMatrix.WHITE_CHAR);
            else
               System.out.print(DataMatrix.BLACK_CHAR);
         }
      }
     
      System.out.print("\n");
   }
   
   //Verifies that the size of the data array is valid
   private boolean checkSize(String[] data)
   {
      int size = data.length;
      if(size > MAX_HEIGHT)   
         return false;
      else 
      {
        for(int i = 0; i < size; i++)
        {
           if(data[i].length() > MAX_WIDTH)
              return false;
        }
      }
     
      return true;
   }
}

class DataMatrix implements BarcodeIO
{
   public static final char BLACK_CHAR = '*';
   public static final char WHITE_CHAR = ' ';
   private BarcodeImage image;
   private String text;
   private int actualWidth;
   private int actualHeight;    
   
   // Default constructor
   public DataMatrix()
   {
      image = new BarcodeImage();  
      text = "";
      actualWidth = 0;
      actualHeight = 0;
   }
   
   // Calls scan method to set image.
   public DataMatrix(BarcodeImage image)
   {
      text = "";
      if(!scan(image)) 
      {
         image = new BarcodeImage();
      }
   }
   
   // Sets the text using the read text method.
   public DataMatrix(String text)
   {
      image = new BarcodeImage(); 
      readText(text);
   }
   
   // Mutator for the text. 
   public boolean readText(String text)
   {
      if(text.length() > BarcodeImage.MAX_WIDTH - 2) 
      {
         this.text = "";
         return false;
      }
      
      this.text = text;
      return true;
   }
   
   // Mutator for the image. Calls clean image method.
   public boolean scan(BarcodeImage image)
   {
      try
      {
         this.image = (BarcodeImage)image.clone();        
      }
      catch(CloneNotSupportedException e)
      {
         return false;
      }
      
      cleanImage();      
      return true;
   }   
   
   //Returns the width of the barcode
   //without counting the whitespace
   public int getActualWidth()
   {
      return actualWidth;
   }
   
   //Returns the height of the barcode
   //without counting the whitespace
   public int getActualHeight()
   {
      return actualHeight;
   }
   
   //This function takes a barcode and 
   //translates into its text form. 
   public boolean generateImageFromText()
   {     
      String[] newArray = new String[10];
      boolean[] textToColumn = new boolean[10];
      
      try
      {
         //We iterate through the length of the text that is being
         //passed plus 2 to take into account the borders
         for(int i = 0; i < text.length() + 2; i++)
         {
            //The first column is going to be a solid "spine"
            //The second column is going to be a staggered "spine"
            //Between the first and last columns we have our actual message
            //We encode the actual message using the writeCharToCol function
            if (i == 0)                               
               textToColumn = writeCharToCol(i, 'x');
            else if (i == text.length() + 1)         
               textToColumn = writeCharToCol(-1, 'x');
            else 
               textToColumn = writeCharToCol(i, (int)text.charAt(i - 1));
            
            //Once we retrieve our column that represents the character that was
            //passed we add it to the new string array we're building as a new column
            for(int j = newArray.length - 1; j >= 0; j--)
            {
               if(textToColumn[j])
               {
                  if(newArray[j] == null)
                     newArray[j] = "*";
                  else
                     newArray[j] += "*";
               }
               else
               {
                  if(newArray[j] == null)
                     newArray[j] = " ";
                  else
                   newArray[j] += " ";
               }
            }
         }
         
         //Once we have formed our new string array with our columns 
         //that each represent a character, then we create a new image
         //object that will hold our data.
         scan(new BarcodeImage(newArray));
         
         //If everything is successful we return true
         return true;
      }
      catch(Exception e)
      {
         //return false if something breaks
         return false;
      }
   }
   
   //This function calls the readCharFromCol
   //function to form the string that the 
   //bardcode inside the image object represents
   public boolean translateImageToText()
   {
      int width  = getActualWidth() - 1;
      String tempText = "";
      
      for(int i = 1; i < width; i++)
         tempText += readCharFromCol(i);
      
      text = tempText;
            
      return true;
   }
   
   //This function displays the current text
   public void displayTextToConsole()
   {
      System.out.println(text);
   }
   
   //This function iterates through the contents of the
   //barcode inside of the image object and displays it.
   //The barcode is surrounded by borders
   public void displayImageToConsole()
   {
      int height = getActualHeight();
      int width  = getActualWidth();     
      int start = BarcodeImage.MAX_HEIGHT - height;
      
      for(int i = start; i < BarcodeImage.MAX_HEIGHT; i++)
      {
         if(i == start)
         {
            for(int x = 0; x <= width + 1; x++)
               System.out.print("-");
            
            System.out.println("");
         }
         
         System.out.print("|");

         for(int j = 0; j < width; j++)
         {
            if(image.getPixel(i, j) == true)
               System.out.print(BLACK_CHAR);
            else
               System.out.print(WHITE_CHAR);
         }         
         
         System.out.println("|");
      }
      
      System.out.println("");
   }   
   
   
   // Calls moveImageToLowerLeft method and sets width and height.
   private void cleanImage()
   {
      moveImageToLowerLeft();
      this.actualWidth = computeSignalWidth();
      this.actualHeight = computeSignalHeight();
   }
   
   //Uses getLeftColumn and getBottomRow methods to find left and bottom borders.
   //Once they have been located the image will be set to that location and moved.
   private void moveImageToLowerLeft()
   {
      BarcodeImage image = new BarcodeImage();
      
      int column = getLeftColumn();
      int row = getBottomRow();
      int offset = (BarcodeImage.MAX_HEIGHT - 1) - row;
      
      for(int i = BarcodeImage.MAX_HEIGHT - 1; i >= 0; i--)
      {
         for(int j = 0; j < BarcodeImage.MAX_WIDTH - 1; j++)
         {
            image.setPixel(i, j, this.image.getPixel((i - offset), (j + column)));
         }
      }

      this.image = image;
   }
   
   // Returns the leftmost column.
   private int getLeftColumn()
   {      
      for(int i = 0; i < BarcodeImage.MAX_HEIGHT; i++) 
      {
         for(int j = 0; j < BarcodeImage.MAX_WIDTH; j++)
         {
            if(image.getPixel(i, j))
            {
               return j;
            }
         }
      }
     
      return BarcodeImage.MAX_WIDTH;
   }
   
   // Uses getLeftColumn method to find the bottom row and returns row.
   private int getBottomRow()
   {
      int column = getLeftColumn();
      int row = 0;
      
      for(int i = 0; i < BarcodeImage.MAX_HEIGHT; i++)
      {
         if(image.getPixel(i, column))
         {
            row = i;
         }
      }
     
      return row;
   }
   
   //This function takes a column from the image
   //object and turns it into its char value 
   private char readCharFromCol(int col)
   {
      int start = BarcodeImage.MAX_HEIGHT - 2;
      int end = BarcodeImage.MAX_HEIGHT - getActualHeight() + 1;
      int asciiValue = 0;
      int powerOfTwo = 1;
      
      //Algorithm for deciphering the column of the barcode
      //and finding out what ASCII character it represents
      for(int i = start; i >= end; i--)
      {        
         if(image.getPixel(i, col) == true)
            asciiValue += powerOfTwo;
         else
            asciiValue += 0;
         
         powerOfTwo *= 2;
      }
      
      return (char)asciiValue;
   }
   
   //This function takes a character from a string and 
   //turns it into its barcode column representation
   private boolean[] writeCharToCol(int col, int code)
   {
      boolean[] column = new boolean[10];
      int binary = 128;
      
      //If we're translating the first column then we need
      //to first add the left spine of the image
      if(col == 0)
         return new boolean[] {true, true, true, true, true,
                               true, true, true, true, true};
      //A col of -1 indicates that this is the last column
      //A staggered true false column is returned
      if(col == -1)
         return new boolean[] {false, true, false, true, false,
                               true, false, true, false, true};
      
      //Add the bottom spine of the image
      column[9] = true;
      
      //Translate the code into a boolean array
      //represented by true and false.
      //1 = true, 0 = false 
      for(int i = 1; i <= 8; i++)
      {
         if(code >= binary)
         {
            column[i] = true;
            code -= binary;
         }
         else
            column[i] = false;
         
         binary /= 2;
      }
      
      //This ads the upper staggered "spine" to the column
      if(col%2 == 0)
         column[0] = true;
      
      return column;
   }
   
   //This function returns the width of the barcode
   //by measuring the characters in the bottom "spine"
   private int computeSignalWidth()
   {      
      for(int i = 0; i < BarcodeImage.MAX_WIDTH; i++)
      {
          if(!image.getPixel(BarcodeImage.MAX_HEIGHT - 1, i))
          {
             return i;
          }          
      }
      
      return BarcodeImage.MAX_WIDTH;
   }

   //This function returns the height of the barcode
   //by measuring the characters of the left "spine"
   private int computeSignalHeight()
   {
      for(int i = BarcodeImage.MAX_HEIGHT - 1; i > 0 ; i--)
      {
          if(!image.getPixel(i, 0))
          {
             return BarcodeImage.MAX_HEIGHT - 1 - i;
          }          
      }
      
      return BarcodeImage.MAX_HEIGHT;
   }
}

/* -------------------- Sample Run ---------------------------* 
CSUMB CSIT online program is top notch.
-------------------------------------------
|* * * * * * * * * * * * * * * * * * * * *|
|*                                       *|
|****** **** ****** ******* ** *** *****  |
|*     *    ******************************|
|* **    * *        **  *    * * *   *    |
|*   *    *  *****    *   * *   *  **  ***|
|*  **     * *** **   **  *    **  ***  * |
|***  * **   **  *   ****    *  *  ** * **|
|*****  ***  *  * *   ** ** **  *   * *   |
|*****************************************|

You did it!  Great work.  Celebrate.
----------------------------------------
|* * * * * * * * * * * * * * * * * * * |
|*                                    *|
|**** *** **   ***** ****   *********  |
|* ************ ************ **********|
|** *      *    *  * * *         * *   |
|***   *  *           * **    *      **|
|* ** * *  *   * * * **  *   ***   *** |
|* *           **    *****  *   **   **|
|****  *  * *  * **  ** *   ** *  * *  |
|**************************************|

What a great resume builder this is!
----------------------------------------
|* * * * * * * * * * * * * * * * * * * |
|*                                    *|
|***** * ***** ****** ******* **** **  |
|* ************************************|
|**  *    *  * * **    *    * *  *  *  |
|* *               *    **     **  *  *|
|**  *   * * *  * ***  * ***  *        |
|**      **    * *    *     *    *  * *|
|** *  * * **   *****  **  *    ** *** |
|**************************************|

Test message: Osprey Corp is the best Corp
----------------------------------------------
|* * * * * * * * * * * * * * * * * * * * * * |
|*                                          *|
|***** *******  ****** **** ** *** **** **** |
|* ************* ****** **************** ****|
|** **   **   *  *** *   **  * *     **   ** |
|*     *      * *    *  *   *   *        *  *|
|*** * **   **  *   *   *      * *  * *  *   |
|*  *    ** * * ** *   ***   *     * *  *** *|
|* **  *******  **  ** **   **   *  **  **   |
|********************************************|
-------------------------------------------------------- */
