package com.tfpower.arraydbs.entity;

import java.math.BigDecimal;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * Created by vlad on 24.01.18.
 */
public class JoinReport {
    private String joinerName;
    private GraphDescription graphName;
    private List<String> traverseSequence;
    private Map<String, Integer> loadFrequencies;
    private Integer totalWeight;
    IntSummaryStatistics loadFreqStats;


    public JoinReport(String joinerName, GraphDescription graphDescription, List<String> traverseSequence, Map<String, Integer> loadFrequencies, Integer totalWeight) {
        this.joinerName = joinerName;
        this.graphName = graphDescription;
        this.traverseSequence = traverseSequence;
        this.loadFrequencies = loadFrequencies;
        this.totalWeight = totalWeight;
    }


    public List<String> getTraverseSequence() {
        return traverseSequence;
    }

    public Map<String, Integer> getLoadFrequencies() {
        return loadFrequencies;
    }

    public Integer getTotalWeight() {
        return totalWeight;
    }
    public String getJoinerName() {
        return joinerName;
    }

    private void computeLoadFreqStats() {
        loadFreqStats = loadFrequencies.values().stream().mapToInt(Integer::intValue).summaryStatistics();
    }

    @Override
    public String toString() {

        return "JoinReport{\n\t" +
                    "'joinedBy':"         + joinerName       + ",\n\t" +
                    "'graphName':"        + graphName        + ",\n\t" +
                    "'totalWeight':"      + totalWeight      + "\n\t" +
                    "'traverseSequence':" + traverseSequence + ",\n\t" +
                    "'loadFrequencies':"  + loadFrequencies  + ",\n\t" +
                    "'loadFreqStats':"    + loadFreqStats    + "\n\t" +
                '}';
    }


    public static JoinReport fromGraphTraversal(TraverseHelper traversal, String joinerName, GraphDescription graphDescription){
        JoinReport joinReport = new JoinReport(
                joinerName,
                graphDescription,
                traversal.getVisitResult().stream().map(Vertex::getId).collect(Collectors.toList()),
                new TreeMap<>(traversal.getVisitCountsPerVertices()),
                traversal.getAccumulator()
        );
        joinReport.computeLoadFreqStats();
        return joinReport;
    }


    public static JoinReportDiff diff(JoinReport joinReportBase, JoinReport joinReportRival) {
        final int ratioScale = 2;
        long baseCount = joinReportBase.loadFreqStats.getCount();
        int baseMin = joinReportBase.loadFreqStats.getMin();
        int baseMax = joinReportBase.loadFreqStats.getMax();
        int baseWeight = joinReportBase.getTotalWeight();
        double baseAvg = joinReportBase.loadFreqStats.getAverage();
        double ratioCount  = (baseCount == 0)   ? Double.MAX_VALUE : (double) joinReportRival.loadFreqStats.getCount()   / baseCount;
        double ratioMin    = (baseMin == 0 )    ? Double.MAX_VALUE : (double) joinReportRival.loadFreqStats.getMin()     / baseMin;
        double ratioAvg    = (baseAvg == 0 )    ? Double.MAX_VALUE :          joinReportRival.loadFreqStats.getAverage() / baseAvg;
        double ratioMax    = (baseMax == 0 )    ? Double.MAX_VALUE : (double) joinReportRival.loadFreqStats.getMax()     / baseMax;
        double ratioWeight = (baseWeight == 0 ) ? Double.MAX_VALUE : (double) joinReportRival.getTotalWeight()           / baseWeight;
        return new JoinReportDiff(joinReportBase.getJoinerName(), joinReportRival.getJoinerName(),
                baseCount, baseMin, baseAvg, baseMax, baseWeight,
                new BigDecimal(ratioCount).setScale(ratioScale, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(ratioMin).setScale(ratioScale, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(ratioAvg).setScale(ratioScale, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(ratioMax).setScale(ratioScale, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(ratioWeight).setScale(ratioScale, BigDecimal.ROUND_HALF_UP)
        );
    }


    public static class JoinReportDiff {
        private String baseJoinerName;
        private String rivalJoinerName;
        private Long baseLoadAmount;
        private Integer baseLoadMin;
        private Double baseLoadAvg;
        private Integer baseLoadMax;
        private Integer baseLoadWeight;
        private BigDecimal ratioLoadAmount;
        private BigDecimal ratioMinLoad;
        private BigDecimal ratioAvgLoad;
        private BigDecimal ratioMaxLoad;
        private BigDecimal ratioWeight;


        JoinReportDiff(String baseJoinerName, String rivalJoinerName,
                       Long baseLoadAmount, Integer baseLoadMin, Double baseLoadAvg, Integer baseLoadMax, Integer baseLoadWeight,
                       BigDecimal ratioLoadAmount, BigDecimal ratioMinLoad,  BigDecimal ratioAvgLoad, BigDecimal ratioMaxLoad, BigDecimal ratioWeight) {
            this.baseJoinerName = baseJoinerName;
            this.rivalJoinerName = rivalJoinerName;
            this.baseLoadAmount = baseLoadAmount;
            this.baseLoadMin = baseLoadMin;
            this.baseLoadAvg = baseLoadAvg;
            this.baseLoadMax = baseLoadMax;
            this.baseLoadWeight = baseLoadWeight;
            this.ratioLoadAmount = ratioLoadAmount;
            this.ratioWeight = ratioWeight;
            this.ratioMinLoad = ratioMinLoad;
            this.ratioAvgLoad = ratioAvgLoad;
            this.ratioMaxLoad = ratioMaxLoad;
        }


        public String getBaseJoinerName() {
            return baseJoinerName;
        }


        public String getRivalJoinerName() {
            return rivalJoinerName;
        }


        public Long getBaseLoadAmount() {
            return baseLoadAmount;
        }


        public Integer getBaseLoadMin() {
            return baseLoadMin;
        }


        public Double getBaseLoadAvg() {
            return baseLoadAvg;
        }


        public Integer getBaseLoadMax() {
            return baseLoadMax;
        }


        public Integer getBaseLoadWeight() {
            return baseLoadWeight;
        }


        public BigDecimal getRatioLoadAmount() {
            return ratioLoadAmount;
        }


        public BigDecimal getRatioWeight() {
            return ratioWeight;
        }


        public BigDecimal getRatioMinLoad() {
            return ratioMinLoad;
        }


        public BigDecimal getRatioAvgLoad() {
            return ratioAvgLoad;
        }


        public BigDecimal getRatioMaxLoad() {
            return ratioMaxLoad;
        }

        public String toStringCsv(String separator){
            return Stream.of(
                    baseJoinerName,
                    rivalJoinerName,
                    baseLoadAmount.toString(),
                    baseLoadMin.toString(),
                    baseLoadAvg.toString(),
                    baseLoadMax.toString(),
                    baseLoadWeight.toString(),
                    ratioLoadAmount.toString(),
                    ratioMinLoad.toString(),
                    ratioAvgLoad.toString(),
                    ratioMaxLoad.toString(),
                    ratioWeight.toString()
            ).collect(joining(separator));
        }

    }


}
