package musicEd.music;

import java.time.YearMonth;

import musicEd.reaction.Mass;
import java.awt.*;

public class Beam extends Mass{
    public Stem.List stems = new Stem.List();
    public static Polygon poly;
    public static int mx1, my1, mx2, my2;

    static{
        int [] foo = {0,0,0,0};
        poly = new Polygon(foo, foo, 4);
    }

    public Beam(Stem stem1, Stem stem2){
        super("NOTE");
        addStem(stem1);
        addStem(stem2);
    }

    public void addStem(Stem s) {
        if (s.beam == null) {
            stems.add(s);
            s.beam = this;
            s.nFlag = 1;
            stems.sort();
        }
    }

    public Stem first() {return stems.get(0);}
    public Stem last() {return stems.get(stems.size() - 1);}
    public void deleteBeam() {
        for (Stem s: stems) {s.beam = null;}
        deleteMass(); // remove beam from layers
    }
    
    public void setMasterBeam() {
        mx1 = first().X();
        my1 = first().yBeamEnd();
        mx2 = last().X();
        my2 = last().yBeamEnd();
    }

    public void show(Graphics g) {g.setColor(Color.BLACK); drawBeamGroup(g);}
    private void drawBeamGroup(Graphics g) {
        setMasterBeam();
        Stem firstStem = first();
        int H = firstStem.staff.H(); int sH = firstStem.isUp? H : -H; // signed H needed for beam stack
        int nPrev = 0, nCurr = firstStem.nFlag, nNext = stems.get(1).nFlag; // flag counts on previous, current, and next
        int px; int cx = firstStem.X(); // the x location of previous stem and current stem
        int bx = cx + 3*H; // forward leading beamlet
        if(nCurr > nNext) {drawBeamStack(g, nNext, nCurr, cx, bx, sH);}
        for(int cur = 1; cur < stems.size(); cur++) {
            Stem sCur = stems.get(cur); px = cx; cx = sCur.X();
            nPrev = nCurr; nCurr = nNext; nNext = (cur < stems.size() - 1)? stems.get(cur+1).nFlag: 0;
            int nBack = Math.min(nPrev, nCurr);
            drawBeamStack(g, 0, nBack, px, cx, sH); // These are beams back to previous stem
            if(nCurr > nPrev && nCurr > nNext) { //need beamlet on curent stem
                if(nPrev < nNext) { // beamlet lean towards signed beams
                    bx = cx + 3 * H;
                    drawBeamStack(g, nNext, nCurr, cx, bx, sH);
                }else {
                    bx = cx - 3 * H;
                    drawBeamStack(g, nPrev, nCurr, bx, cx, sH);
                }
            }
        }

    }

    public static int yOfX(int x, int x1, int y1, int x2, int y2) {
        int dy = y2 - y1, dx = x2 - x1;
        return (x - x1)*dy / dx + y1;
    }

    public static int yOfX(int x) {return yOfX(x, mx1, my1, mx2, my2);}
    public static void setMasterBeam(int x1, int y1, int x2, int y2) {
        mx1 = x1; my1 = y1; mx2 = x2; my2 = y2;
    }

    public static boolean verticalLineCrossesSegment(int x, int y1, int y2, int bx, int by, int ex, int ey) {
        if(x < bx || x > ex) {return false;}
        int y = yOfX(x, bx, by, ex, ey);
        if(y1 < y2) {return y1 < y && y < y2;} else {return y2 < y && y < y1;}

    }

    public static void setPoly(int x1, int y1, int x2, int y2, int h) {
        int[] a = poly.xpoints;
        a[0] = x1; a[1] = x2; a[2] = x2; a[3] = x1;
        a = poly.ypoints;
        a[0] = y1; a[1] = y2; a[2] = y2 + h; a[3] = y1 + h;
    }

    public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int h) {
        int y1 = yOfX(x1), y2 = yOfX(x2);
        for (int i = n1; i < n2; i++) {
            setPoly(x1, y1+i*2*h, x2, y2+i*2*h, h);
            g.fillPolygon(poly);
        }
    }
    
}
