/**
 *Copyright (C) 2013 Alex Rodrigues
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 *software and associated documentation files (the "Software"), to deal in the Software without 
 *restriction, including without limitation the rights to use, copy, modify, merge, publish, 
 *distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom 
 *the Software is furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all copies or 
 *substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 *PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
 *ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 *ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 *SOFTWARE.
 * 
 * Author: Alex Rodrigues
 */

/**
 * Utility class that emulates a tuple.
 */
public class Pair <T1, T2>
{
   private final T1 first;
   private final T2 second;
   private transient final int hash;

   public Pair( T1 f, T2 s )
   {
    this.first = f;
    this.second = s;
    hash = (first == null? 0 : first.hashCode() * 31)
          +(second == null? 0 : second.hashCode());
   }

   public T1 getFirst()
   {
    return first;
   }
   public T2 getSecond()
   {
    return second;
   }

   @Override
   public int hashCode()
   {
    return hash;
   }

   @Override
   public boolean equals( Object otherObj )
   {
    if ( this == otherObj )
    {
      return true;
    }
    if ( otherObj == null || !(getClass().isInstance( otherObj )) )
    {
      return false;
    }
    Pair<T1, T2> otherPair = getClass().cast( otherObj );
    return (first == null? otherPair.first == null : first.equals( otherPair.first ))
     && (second == null? otherPair.second == null : second.equals( otherPair.second ));
   }

}
