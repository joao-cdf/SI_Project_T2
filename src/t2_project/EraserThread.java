package t2_project;

class EraserThread implements Runnable {
    
    private boolean stop;
 

    public EraserThread(String prompt) {
        System.out.print(prompt);
    }

    @Override
    public void run () {
        stop = true;
        while (stop) {
            System.out.print("\010*");
            try {
                Thread.sleep(1);
            } catch(InterruptedException ie) {
            }
        }
    }

    public void stopMasking() {
        this.stop = false;
    }
    
}
