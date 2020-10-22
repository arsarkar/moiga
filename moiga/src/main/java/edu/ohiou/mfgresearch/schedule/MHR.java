package edu.ohiou.mfgresearch.schedule;

import org.moeaframework.core.Variable;

import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic.Heuristic;

/**
 * Model-based Hybrid Representation
 */
    public class MHR implements Variable{

        private static final long serialVersionUID = -1893024648917611956L;
        Heuristic h;
        int p;

		public MHR(Heuristic h, int p) {
			this.h = h;
			this.p = p;
        }   

        @Override
        public Variable copy() {
            return new MHR(h, p);
        }

        @Override
        public void randomize() {
        }

        public Heuristic getH() {
            return h;
        }

        public void setH(Heuristic h) {
            this.h = h;
        }

        public int getP() {
            return p;
        }

        public void setP(int p) {
            this.p = p;
        }        
    } 
