package tap.execounting.data;

public enum EventTransferType {
   SCHEDULED(0),
   EXACT_DATE(1);

   private int value;

   private EventTransferType(int value){
      this.value = value;
   }

   public int toInt(){
      return value;
   }
}
