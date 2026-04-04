import { create } from 'zustand';
import { financeService } from '../services/financeService';

export const useFinanceStore = create((set) => ({
  dashboard: null,
  transactions: [],
  isLoading: false,
  error: null,
  rowErrors: [],
  successMsg: null,
  
  // 🛡️ [INTEGRITY] Normalization Adapter Layer
  normalizeDashboardData: (data) => {
    if (!data) return null;
    
    // Bridge between Backend v3 (CamelCase) and Backend v4 (snake_case + specific requirements)
    const normalized = {
      has_data: data.has_data ?? (data.safeToSpend > 0 || data.safe_spend > 0),
      safe_spend: data.safe_spend ?? data.safeToSpend ?? 0,
      emergency_buffer: data.emergency_buffer ?? data.score ?? 0,
      stability: data.stability ?? (data.reliability > 70 ? "Stable" : (data.reliability > 40 ? "Evolving" : "Volatile")),
      volatility: data.volatility ?? (data.volatility_score ? Math.round(data.volatility_score * 100) : 0),
      prediction: data.prediction ?? {
        expected: data.forecast ?? 0,
        min: data.forecast_min ?? 0,
        max: data.forecast_max ?? 0
      },
      insights: data.insights ?? ["No patterns detected yet."],
      reliability: data.reliability ?? 0,
      forecast_expense: data.forecast_expense ?? 0,
      forecast_series: data.forecast_series ?? data.forecastSeries ?? []
    };

    console.log("🛠️ [ADAPTER] Unified financial state generated:", normalized);
    return normalized;
  },

  fetchDashboard: async () => {
    const { normalizeDashboardData } = useFinanceStore.getState();
    set({ isLoading: true });
    try {
      const [rawDashboard, transactions] = await Promise.all([
        financeService.getBuffer(),
        financeService.getTransactions()
      ]);
      const dashboard = normalizeDashboardData(rawDashboard);
      set({ dashboard, transactions, isLoading: false });
    } catch (err) {
      console.error("❌ [INTEGRITY] Sync failed:", err);
      set({ error: 'System Fetch Failed', isLoading: false });
    }
  },

  uploadSync: async (file) => {
    const { normalizeDashboardData } = useFinanceStore.getState();
    set({ isLoading: true, error: null, rowErrors: [], successMsg: null });
    try {
      console.log("📂 [INTEGRITY] Uploading CSV for full engine re-sync...");
      const response = await financeService.calculate(file);
      const [rawDashboard, transactions] = await Promise.all([
        financeService.getBuffer(),
        financeService.getTransactions()
      ]);
      
      const dashboard = normalizeDashboardData(rawDashboard);
      console.log("📈 [INTEGRITY] Post-upload sync complete:", { dashboard, transactions });

      if (response && response.errors && response.errors.length > 0) {
         set({ rowErrors: response.errors, error: 'Some rows contained errors.', dashboard, transactions, isLoading: false });
      } else {
         set({ dashboard, transactions, isLoading: false, successMsg: "Data updated successfully" });
         // ⏳ Auto-clear success message
         setTimeout(() => set({ successMsg: null }), 5000);
      }
      return true;
    } catch (err) {
      console.error("🏁 [INTEGRITY] Upload failed:", err);
      set({ error: err.message || 'Sync System Offline', rowErrors: [], isLoading: false });
      return false;
    }
  },

  addTransaction: async (tx) => {
    const { normalizeDashboardData } = useFinanceStore.getState();
    set({ isLoading: true, error: null });
    try {
      await financeService.addTransaction(tx);
      const [rawDashboard, transactions] = await Promise.all([
        financeService.getBuffer(),
        financeService.getTransactions()
      ]);
      const dashboard = normalizeDashboardData(rawDashboard);
      set({ dashboard, transactions, isLoading: false });
      return true;
    } catch (err) {
      set({ error: err.message || 'Manual Entry Failed', isLoading: false });
      return false;
    }
  }
}));

export default useFinanceStore;
