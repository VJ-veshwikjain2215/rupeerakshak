import axios from 'axios';

const aiApi = axios.create({
  baseURL: 'http://localhost:5005',
});

export const financeService = {
  getBuffer: async () => {
    try {
      const response = await aiApi.get('/dashboard');
      return response.data;
    } catch (err) {
      if (!err.response) {
        console.warn('Backend Offline. Visualizing Financial Projections via Cloud Emulation.');
        return {
          forecast: 0,
          taxBuffer: 0,
          safeToSpend: 0,
          score: 0,
          confidence: 0,
          forecastSeries: [],
          insights: ["EMULATION: Manual Sync Required."]
        };
      }
      throw new Error(err.response?.data?.message || 'Sync System Refused Connection');
    }
  },

  calculate: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    try {
      console.log(`📂 [UPLOAD] Sending ${file.name} (${file.size} bytes) to /upload-csv`);
      const response = await aiApi.post('/upload-csv', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      console.log('✅ [UPLOAD] Response:', response.data);
      return response.data;
    } catch (err) {
      console.error('❌ [UPLOAD] Error:', err);
      if (!err.response) {
        throw new Error('Upload endpoint not reachable. Is the AI service running on port 5005?');
      }
      if (err.response?.status === 400) {
        throw new Error(err.response?.data?.detail || 'Invalid CSV format or missing columns');
      }
      if (err.response?.status >= 500) {
        throw new Error('Server failed to parse file. Check backend logs.');
      }
      throw new Error(err.response?.data?.detail || err.message || 'Sync System Offline');
    }
  },

  getTransactions: async () => {
    try {
      const response = await aiApi.get('/transactions');
      return response.data || [];
    } catch (err) {
      return [];
    }
  },

  // Minimal manual insert (optional if you still use spring boot for single inserts)
  addTransaction: async (tx) => {
    throw new Error('Manual entries are temporarily paused while CSV Sync manages state.');
  },

  vacationPlanner: async ({ totalBalance, expenses, selectedWeeks = 1, predictedIncome = 0 }) => {
    try {
      const response = await aiApi.post('/vacation-planner', { totalBalance, expenses, selectedWeeks, predictedIncome });
      return response.data;
    } catch (err) {
      // 🧠 [ENGINE] New Redesigned Decision Engine (Production-Level Logic)
      const monthlyExpense = expenses.reduce((s, e) => s + (parseFloat(e.amount) || 0), 0);
      const weeklyExpense  = monthlyExpense / 4;
      const vacationCost   = weeklyExpense * selectedWeeks;
      
      if (monthlyExpense <= 0 || totalBalance <= 0) return { status: 'insufficient', message: 'INSUFFICIENT DATA: Please update your expenses and sync CSV.' };
      
      // 1. Core Variables
      const monthlySurplus   = predictedIncome - monthlyExpense;
      const remainingBalance = totalBalance - vacationCost;
      const newBuffer        = monthlyExpense > 0 ? remainingBalance / monthlyExpense : 10;
      const recoveryTime     = monthlySurplus > 0 ? vacationCost / monthlySurplus : 999;
      
      // 2. Decision Logic (Hierarchical Assessment)
      let status = 'risky';
      let message = '';
      
      if (monthlySurplus <= 0 || newBuffer < 1.5 || recoveryTime > 6 || vacationCost > totalBalance) {
        status = 'dangerous';
        message = `NOT RECOMMENDED: This spend is critical. It leaves you with only ${newBuffer.toFixed(1)} months buffer and ${monthlySurplus <= 0 ? 'no surplus to recover' : 'over 6 months to recover'}.`;
      } else if (newBuffer >= 4 && recoveryTime <= 1) {
        status = 'safe';
        message = `YOU ARE SAFE: Your position remains strong with a ${newBuffer.toFixed(1)} month buffer and full recovery expected in ${recoveryTime.toFixed(1)} months.`;
      } else if (newBuffer >= 2.5 || recoveryTime <= 3) {
        status = 'moderate';
        message = `MANAGEABLE: This is acceptable, but it reduces your buffer to ${newBuffer.toFixed(1)} months with a recovery period of ${recoveryTime.toFixed(1)} months.`;
      } else {
        status = 'risky';
        message = `RISKY: Caution advised. Your buffer drops to ${newBuffer.toFixed(1)} months and it will take ${recoveryTime.toFixed(1)} months to recover this spend.`;
      }

      const maxWeeks         = Math.floor(totalBalance / (weeklyExpense || 1));
      const remainingPct     = totalBalance > 0 ? (remainingBalance / totalBalance) * 100 : 0;
      const availableToSpend = totalBalance - monthlyExpense;
      
      return {
        totalBalance, 
        monthlyExpense, 
        weeklyExpense, 
        availableToSpend,
        selectedWeeks, 
        vacationCost, 
        remainingBalance,
        remainingPct: Math.round(remainingPct * 10) / 10,
        maxWeeks, 
        survivalWeeks: maxWeeks,
        status, 
        message,
        insight: (status === 'dangerous' || status === 'risky') ? "⚠️ High financial stress detected. Priority: Debt prevention and buffer rebuilding." : "✅ Your current runway supports this leisure period.",
        suggestion: {
          reduceMonthlyBy: Math.max(monthlyExpense * 0.1, 0),
          gainWeeks: 1,
          weeksToSaveMore: Math.ceil(weeklyExpense / Math.max(monthlySurplus, 1)),
        }
      };
    }
  }
};

export default financeService;
